using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Globalization;
using OpenTK;
using OpenTK.Graphics.OpenGL;

namespace Whitehole
{
    public static class ModelCache
    {
        public static void Init()
        {
            CultureInfo forceusa = new CultureInfo("en-US");

            m_Cache = new Dictionary<string, Entry>();
            m_ObjectRenderInfo = new Dictionary<string, Dictionary<string, object>>();
            m_ObjectRenderInfo.Add("_default", new Dictionary<string, object>(0));
            m_Tables = new Dictionary<string, Dictionary<string, string>>();
            m_ObjectVisualParameters = new Dictionary<string, List<string>>();

            string[] lines = Properties.Resources.ObjectRenderInfo.Split('\n');
            foreach (string _line in lines)
            {
                string line = _line.Trim();

                if (line.Length < 1) continue;
                if (line[0] == '#') continue;

                if (line[0] == '$')
                {
                    line = line.Substring(1);
                    string tablename = line.Substring(0, line.IndexOf('=')).Trim();
                    string vallist = line.Substring(line.IndexOf('=') + 1);
                    vallist = vallist.Substring(vallist.IndexOf('{') + 1);
                    vallist = vallist.Substring(0, vallist.LastIndexOf('}'));
                    string[] values = vallist.Split(';');

                    m_Tables.Add(tablename, new Dictionary<string, string>());

                    foreach (string val in values)
                    {
                        if (val.Trim().Length < 1) continue;

                        string valkey = val.Substring(0, val.IndexOf(':')).Trim();
                        string valval = val.Substring(val.IndexOf(':') + 1).Trim();

                        m_Tables[tablename].Add(valkey, valval);
                    }
                    
                    continue;
                }

                string objname = line.Substring(0, line.IndexOf(':')).Trim();
                string[] args = line.Substring(line.IndexOf(':') + 1).Split(';');

                Dictionary<string, object> entry = new Dictionary<string, object>();

                foreach (string arg in args)
                {
                    if (arg.Trim().Length < 1) continue;

                    string argname = arg.Substring(0, arg.IndexOf('=')).Trim();
                    string valstring = arg.Substring(arg.IndexOf('=') + 1).Trim();
                    object value = null;

                    string valtype = valstring.Substring(0, valstring.IndexOf('(')).Trim();
                    valstring = valstring.Substring(valstring.IndexOf('(') + 1);
                    string[] valargs = valstring.Substring(0, valstring.IndexOf(')')).Split(',');
                    
                    try
                    {
                        switch (valtype)
                        {
                            case "String":
                                value = valargs[0];
                                break;

                            case "Vector3":
                                value = new Vector3(
                                    float.Parse(valargs[0], forceusa), 
                                    float.Parse(valargs[1], forceusa), 
                                    float.Parse(valargs[2], forceusa));
                                break;
                        }
                    }
                    catch
                    {
                        value = null;
                    }
                    
                    entry.Add(argname, value);
                }

                m_ObjectRenderInfo.Add(objname, entry);
            }
        }

        private static string ParseString(string str, LevelObjectBase obj)
        {
            while (str.Contains("{$"))
            {
                int start = str.IndexOf("{$");
                int end = str.IndexOf('}') + 1;

                string arg = str.Substring(start + 2, end - start - 3);
                string tblname = arg.Substring(0, arg.IndexOf('['));
                string idxname = arg.Substring(arg.IndexOf('[') + 1);
                idxname = idxname.Substring(0, idxname.IndexOf(']'));

                string rep = m_Tables[tblname][obj[idxname].ToString()];

                str = str.Substring(0, start) + rep + str.Substring(end);
            }

            return str;
        }

        public static Entry GetObjectModel(LevelObjectBase obj)
        {
            string objname = (string)obj["name"];
            Dictionary<string, object> renderinfo;
            if (m_ObjectRenderInfo.ContainsKey(objname))
                renderinfo = m_ObjectRenderInfo[objname];
            else
                renderinfo = m_ObjectRenderInfo["_default"];

            string filename;
            if (renderinfo.ContainsKey("FileName"))
                filename = ParseString((string)renderinfo["FileName"], obj);
            else
                filename = (string)obj["name"];

            string key = "Object:" + filename;
            if (m_Cache.ContainsKey(key))
            {
                Entry ce = m_Cache[key];
                ce.m_ReferenceCount++;
                return ce;
            }

            Entry entry = new Entry();
            entry.m_ReferenceCount = 1;
            entry.Key = key;

            FilesystemBase cont;
            RendererBase rend;

            try
            {
                cont = new RarcFilesystem(Program.GameArchive.OpenFile("/ObjectData/" + filename + ".arc"));

                string fullname = "/" + filename + "/" + filename;
                if (cont.FileExists(fullname + ".bmd"))
                    fullname += ".bmd";
                else
                    fullname += ".bdl";

                rend = new BmdRenderer(new Bmd(cont.OpenFile(fullname)));
            }
            catch
            {
                cont = null;
                rend = new ColorCubeRenderer(200f, new Vector4(1f, 1f, 1f, 1f), new Vector4(1f, 0f, 1f, 1f), true);
            }

            entry.Container = cont;
            entry.Renderer = rend;

            entry.DisplayLists = new int[3];
            RenderInfo ri = new RenderInfo();

            for (int i = 0; i < 3; i++)
            {
                ri.Mode = RenderInfo.Modes[i];
                if (rend.GottaRender(ri))
                {
                    entry.DisplayLists[i] = GL.GenLists(1);
                    GL.NewList(entry.DisplayLists[i], ListMode.Compile);

                    if (renderinfo.ContainsKey("Scale"))
                        GL.Scale((Vector3)renderinfo["Scale"]);

                    rend.Render(ri);

                    GL.EndList();
                }
                else entry.DisplayLists[i] = 0;
            }

            m_Cache.Add(key, entry);
            return entry;
        }

        public static void CloseModel(Entry e)
        {
            e.m_ReferenceCount--;

            if (e.m_ReferenceCount < 1)
            {
                GL.DeleteLists(e.DisplayLists[0], 1);
                GL.DeleteLists(e.DisplayLists[1], 1);
                GL.DeleteLists(e.DisplayLists[2], 1);

                e.Renderer.Close();
                if (e.Container != null)
                    e.Container.Close();

                m_Cache.Remove(e.Key);
            }
        }


        public class Entry
        {
            public string Key;

            public RendererBase Renderer;
            public FilesystemBase Container; // null if the renderer doesn't come from an archive (ie ColorCubeRenderer)

            public int[] DisplayLists;

            public int m_ReferenceCount;
        }

        private static Dictionary<string, Entry> m_Cache;
        private static Dictionary<string, Dictionary<string, string>> m_Tables;
        private static Dictionary<string, Dictionary<string, object>> m_ObjectRenderInfo;
        private static Dictionary<string, List<string>> m_ObjectVisualParameters;
    }
}
