using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Whitehole
{
    public class Bcsv
    {
        public Bcsv(FileBase file)
        {
            m_File = file;
            m_File.BigEndian = true;
            m_File.Encoding = Encoding.GetEncoding("shift-jis");

            Fields = new Dictionary<uint, Field>();
            Entries = new List<Entry>();

            m_File.Stream.Position = 0;
            uint entrycount = m_File.Reader.ReadUInt32();
            uint fieldcount = m_File.Reader.ReadUInt32();
            uint dataoffset = m_File.Reader.ReadUInt32();
            uint entrydatasize = m_File.Reader.ReadUInt32();

            uint stringtableoffset = (uint)(dataoffset + (entrycount * entrydatasize));

            for (uint i = 0; i < fieldcount; i++)
            {
                Field field = new Field();
                m_File.Stream.Position = 0x10 + (0xC * i);

                field.NameHash = m_File.Reader.ReadUInt32();
                field.Mask = m_File.Reader.ReadUInt32();
                field.EntryOffset = m_File.Reader.ReadUInt16();
                field.ShiftAmount = m_File.Reader.ReadByte();
                field.Type = m_File.Reader.ReadByte();

                string fieldname = Bcsv.HashToFieldName(field.NameHash);
                field.Name = fieldname;
                Fields.Add(field.NameHash, field);
            }

            for (uint i = 0; i < entrycount; i++)
            {
                Entry entry = new Entry();

                foreach (Field field in Fields.Values)
                {
                    m_File.Stream.Position = dataoffset + (i * entrydatasize) + field.EntryOffset;

                    object val = null;
                    switch (field.Type)
                    {
                        case 0:
                        case 3:
                            val = (uint)((m_File.Reader.ReadUInt32() & field.Mask) >> field.ShiftAmount);
                            break;

                        case 4:
                            val = (ushort)((m_File.Reader.ReadUInt16() & field.Mask) >> field.ShiftAmount);
                            break;

                        case 5:
                            val = (byte)((m_File.Reader.ReadByte() & field.Mask) >> field.ShiftAmount);
                            break;

                        case 2:
                            val = m_File.Reader.ReadSingle();
                            break;

                        case 6:
                            uint str_offset = m_File.Reader.ReadUInt32();
                            m_File.Stream.Position = stringtableoffset + str_offset;
                            val = m_File.ReadString();
                            break;

                        default:
                            throw new NotImplementedException("Bcsv: unsupported data type " + field.Type.ToString());
                    }

                    entry.Add(field.NameHash, val);
                }

                Entries.Add(entry);
            }
        }

        public void Flush()
        {
            int[] datasizes = { 4, -1, 4, 4, 2, 1, 4 };
            uint entrysize = 0;

            foreach (Field field in Fields.Values)
            {
                ushort fieldend = (ushort)(field.EntryOffset + datasizes[field.Type]);
                if (fieldend > entrysize) entrysize = fieldend;
            }

            uint dataoffset = (uint)(0x10 + (0xC * Fields.Count));
            uint stringtableoffset = (uint)(dataoffset + (Entries.Count * entrysize));
            uint curstring = 0;

            m_File.Stream.SetLength(stringtableoffset);

            m_File.Stream.Position = 0;
            m_File.Writer.Write((uint)Entries.Count);
            m_File.Writer.Write((uint)Fields.Count);
            m_File.Writer.Write(dataoffset);
            m_File.Writer.Write(entrysize);

            foreach (Field field in Fields.Values)
            {
                m_File.Writer.Write(field.NameHash);
                m_File.Writer.Write(field.Mask);
                m_File.Writer.Write(field.EntryOffset);
                m_File.Writer.Write(field.ShiftAmount);
                m_File.Writer.Write(field.Type);
            }

            int i = 0;
            Dictionary<string, uint> stringoffsets = new Dictionary<string, uint>();

            foreach (Entry entry in Entries)
            {
                foreach (Field field in Fields.Values)
                {
                    uint valoffset = (uint)(dataoffset + (i * entrysize) + field.EntryOffset);
                    m_File.Stream.Position = valoffset;
                    
                    switch (field.Type)
                    {
                        case 0:
                        case 3:
                            {
                                uint val = m_File.Reader.ReadUInt32();
                                val &= ~field.Mask;
                                val |= (((uint)entry[field.NameHash] << field.ShiftAmount) & field.Mask);

                                m_File.Stream.Position = valoffset;
                                m_File.Writer.Write(val);
                            }
                            break;

                        case 4:
                            {
                                ushort val = m_File.Reader.ReadUInt16();
                                val &= (ushort)(~field.Mask);
                                val |= (ushort)(((ushort)entry[field.NameHash] << field.ShiftAmount) & field.Mask);

                                m_File.Stream.Position = valoffset;
                                m_File.Writer.Write(val);
                            }
                            break;

                        case 5:
                            {
                                byte val = m_File.Reader.ReadByte();
                                val &= (byte)(~field.Mask);
                                val |= (byte)(((byte)entry[field.NameHash] << field.ShiftAmount) & field.Mask);

                                m_File.Stream.Position = valoffset;
                                m_File.Writer.Write(val);
                            }
                            break;

                        case 2:
                            m_File.Writer.Write((float)entry[field.NameHash]);
                            break;

                        case 6:
                            {
                                string val = (string)entry[field.NameHash];
                                if (stringoffsets.ContainsKey(val))
                                    m_File.Writer.Write(stringoffsets[val]);
                                else
                                {
                                    stringoffsets.Add(val, curstring);
                                    m_File.Writer.Write(curstring);
                                    m_File.Stream.Position = stringtableoffset + curstring;
                                    curstring += (uint)m_File.WriteString(val);
                                }
                            }
                            break;
                    }
                }

                i++;
            }

            m_File.Flush();
        }

        public void Close()
        {
            m_File.Close();
        }


        public Field AddField(string name, int offset, byte type, uint mask, int shift, object defaultval)
        {
            int[] datasizes = { 4, -1, 4, 4, 2, 1, 4 };

            AddHash(name); // hehe

            int nbytes = datasizes[type];

            if (type == 2 || type == 6)
            {
                mask = 0xFFFFFFFF;
                shift = 0;
            }

            if (offset == -1)
            {
                foreach (Field field in Fields.Values)
                {
                    ushort fieldend = (ushort)(field.EntryOffset + datasizes[field.Type]);
                    if (fieldend > offset) offset = fieldend;
                }
            }

            Field newfield = new Field();
            newfield.Name = name;
            newfield.NameHash = Bcsv.FieldNameToHash(name);
            newfield.Mask = mask;
            newfield.ShiftAmount = (byte)shift;
            newfield.Type = type;
            newfield.EntryOffset = (ushort)offset;
            Fields.Add(newfield.NameHash, newfield);

            foreach (Entry entry in Entries)
            {
                entry.Add(name, defaultval);
            }

            return newfield;
        }

        public void RemoveField(string name)
        {
            uint hash = Bcsv.FieldNameToHash(name);
            Fields.Remove(hash);

            foreach (Entry entry in Entries)
            {
                entry.Remove(hash);
            }
        }


        public class Field
        {
            public uint NameHash;
            public uint Mask;
            public ushort EntryOffset;
            public byte ShiftAmount;
            public byte Type;

            public string Name;
        }

        public class Entry : Dictionary<uint, object>
        {
            public Entry()
                : base()
            { }

            public object this[string key]
            {
                get
                {
                    return this[Bcsv.FieldNameToHash(key)];
                }
                set
                {
                    this[Bcsv.FieldNameToHash(key)] = value;
                }
            }

            public void Add(string key, object val)
            {
                this.Add(Bcsv.FieldNameToHash(key), val);
            }

            public bool ContainsKey(string key)
            {
                return this.ContainsKey(Bcsv.FieldNameToHash(key));
            }

            public override string ToString()
            {
                string str = "BcsvEntry:";

                foreach (KeyValuePair<uint, object> field in this)
                {
                    str += " [" + field.Key.ToString("X8");
                    if (Bcsv.m_HashTable.ContainsKey(field.Key))
                        str += " (" + Bcsv.HashToFieldName(field.Key) + ")";
                    str += "]=[" + field.Value.ToString() + "]";
                }

                return str;
            }
        }


        private FileBase m_File;

        public Dictionary<uint, Field> Fields;
        public List<Entry> Entries;


        // Field name hash support functions
        // the hash->string table is meant for debugging purposes and
        // shouldn't be used by proper code

        public static uint FieldNameToHash(string field)
        {
            uint ret = 0;
            foreach (char ch in field)
            {
                ret *= 0x1F;
                ret += ch;
            }
            return ret;
        }

        public static string HashToFieldName(uint hash)
        {
            if (!m_HashTable.ContainsKey(hash))
                return string.Format("[{0:X8}]", hash);

            return m_HashTable[hash];
        }

        public static void AddHash(string field)
        {
            uint hash = FieldNameToHash(field);
            if (!m_HashTable.ContainsKey(hash))
                m_HashTable.Add(hash, field);
        }

        public static void PopulateHashtable()
        {
            m_HashTable = new Dictionary<uint, string>();

            string[] lines = Properties.Resources.KnownFieldNames.Split('\n');
            foreach (string _line in lines)
            {
                string line = _line.Trim();

                if (line.Length == 0) continue;
                if (line[0] == '#') continue;

                AddHash(line);
            }
        }

        public static Dictionary<uint, string> m_HashTable; 
    }
}
