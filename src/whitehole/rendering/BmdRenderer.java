/*
    Copyright 2012 Mega-Mario

    This file is part of Whitehole.

    Whitehole is free software: you can redistribute it and/or modify it under
    the terms of the GNU General Public License as published by the Free
    Software Foundation, either version 3 of the License, or (at your option)
    any later version.

    Whitehole is distributed in the hope that it will be useful, but WITHOUT ANY 
    WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
    FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along 
    with Whitehole. If not, see http://www.gnu.org/licenses/.
*/

package whitehole.rendering;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.Locale;
import javax.media.opengl.*;
import whitehole.*;
import whitehole.smg.*;
import whitehole.vectors.*;

public class BmdRenderer extends GLRenderer
{
    private void uploadTexture(GL2 gl, int id)
    {
        int[] wrapmodes = { GL2.GL_CLAMP_TO_EDGE, GL2.GL_REPEAT, GL2.GL_MIRRORED_REPEAT };
        int[] minfilters = { GL2.GL_NEAREST, GL2.GL_LINEAR,
                             GL2.GL_NEAREST_MIPMAP_NEAREST, GL2.GL_LINEAR_MIPMAP_NEAREST,
                             GL2.GL_NEAREST_MIPMAP_LINEAR, GL2.GL_LINEAR_MIPMAP_LINEAR };
        int[] magfilters = { GL2.GL_NEAREST, GL2.GL_LINEAR,
                             GL2.GL_NEAREST, GL2.GL_LINEAR,
                             GL2.GL_NEAREST, GL2.GL_LINEAR, };

        Bmd.Texture tex = model.textures[id];
        int[] texids = new int[1];
        gl.glGenTextures(1, texids, 0);
        int texid = texids[0];
        textures[id] = texid;

        gl.glBindTexture(GL2.GL_TEXTURE_2D, texid);

        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAX_LEVEL, tex.mipmapCount - 1);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, minfilters[tex.minFilter]);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, magfilters[tex.magFilter]);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, wrapmodes[tex.wrapS]);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, wrapmodes[tex.wrapT]);

        int ifmt, fmt;
        switch (tex.format)
        {
            case 0:
            case 1: ifmt = GL2.GL_INTENSITY; fmt = GL2.GL_LUMINANCE; break;

            case 2:
            case 3: ifmt = GL2.GL_LUMINANCE8_ALPHA8; fmt = GL2.GL_LUMINANCE_ALPHA; break;

            default: ifmt = 4; fmt = GL2.GL_BGRA; break;
        }

        int width = tex.width, height = tex.height;
        for (int mip = 0; mip < tex.mipmapCount; mip++)
        {
            gl.glTexImage2D(GL2.GL_TEXTURE_2D, mip, ifmt, width, height, 0, fmt, GL2.GL_UNSIGNED_BYTE, ByteBuffer.wrap(tex.image[mip]));
            width /= 2; height /= 2;
        }
    }

    private void generateShaders(GL2 gl, int matid) throws GLException
    {
        Locale usa = new Locale("en-US");
        
        String[] texgensrc = { "normalize(gl_Vertex)", "vec4(gl_Normal,1.0)", "argh", "argh",
                                    "gl_MultiTexCoord0", "gl_MultiTexCoord1", "gl_MultiTexCoord2", "gl_MultiTexCoord3",
                                    "gl_MultiTexCoord4", "gl_MultiTexCoord5", "gl_MultiTexCoord6", "gl_MultiTexCoord7" };

        String[] outputregs = { "rprev", "r0", "r1", "r2" };

        String[] c_inputregs = { "truncc3(rprev.rgb)", "truncc3(rprev.aaa)", "truncc3(r0.rgb)", "truncc3(r0.aaa)", 
                                    "truncc3(r1.rgb)", "truncc3(r1.aaa)", "truncc3(r2.rgb)", "truncc3(r2.aaa)",
                                    "texcolor.rgb", "texcolor.aaa", "rascolor.rgb", "rascolor.aaa", 
                                    "vec3(1.0,1.0,1.0)", "vec3(0.5,0.5,0.5)", "konst.rgb", "vec3(0.0,0.0,0.0)" };
        String[] c_inputregsD = { "rprev.rgb", "rprev.aaa", "r0.rgb", "r0.aaa", 
                                    "r1.rgb", "r1.aaa", "r2.rgb", "r2.aaa",
                                    "texcolor.rgb", "texcolor.aaa", "rascolor.rgb", "rascolor.aaa", 
                                    "vec3(1.0,1.0,1.0)", "vec3(0.5,0.5,0.5)", "konst.rgb", "vec3(0.0,0.0,0.0)" };
        String[] c_konstsel = { "vec3(1.0,1.0,1.0)", "vec3(0.875,0.875,0.875)", "vec3(0.75,0.75,0.75)", "vec3(0.625,0.625,0.625)",
                                    "vec3(0.5,0.5,0.5)", "vec3(0.375,0.375,0.375)", "vec3(0.25,0.25,0.25)", "vec3(0.125,0.125,0.125)",
                                    "", "", "", "", "k0.rgb", "k1.rgb", "k2.rgb", "k3.rgb",
                                    "k0.rrr", "k1.rrr", "k2.rrr", "k3.rrr", "k0.ggg", "k1.ggg", "k2.ggg", "k3.ggg",
                                    "k0.bbb", "k1.bbb", "k2.bbb", "k3.bbb", "k0.aaa", "k1.aaa", "k2.aaa", "k3.aaa" };

        String[] a_inputregs = { "truncc1(rprev.a)", "truncc1(r0.a)", "truncc1(r1.a)", "truncc1(r2.a)",
                                    "texcolor.a", "rascolor.a", "konst.a", "0.0" };
        String[] a_inputregsD = { "rprev.a", "r0.a", "r1.a", "r2.a",
                                    "texcolor.a", "rascolor.a", "konst.a", "0.0" };
        String[] a_konstsel = { "1.0", "0.875", "0.75", "0.625", "0.5", "0.375", "0.25", "0.125",
                                    "", "", "", "", "", "", "", "",
                                    "k0.r", "k1.r", "k2.r", "k3.r", "k0.g", "k1.g", "k2.g", "k3.g",
                                    "k0.b", "k1.b", "k2.b", "k3.b", "k0.a", "k1.a", "k2.a", "k3.a" };

        String[] tevbias = { "0.0", "0.5", "-0.5", "## ILLEGAL TEV BIAS ##" };
        String[] tevscale = { "1.0", "2.0", "4.0", "0.5" };

        String[] alphacompare = { "0 == 1", "%1$s < %2$f", "%1$s == %2$f", "%1$s <= %2$f", "%1$s > %2$f", "%1$s != %2$f", "%1$s >= %2$f", "1 == 1" };
        // String[] alphacombine = { "all(bvec2(%1$s,%2$s))", "any(bvec2(%1$s,%2$s))", "any(bvec2(all(bvec2(%1$s,!%2$s)),all(bvec2(!%1$s,%2$s))))", "any(bvec2(all(bvec2(%1$s,%2$s)),all(bvec2(!%1$s,!%2$s))))" };
        String[] alphacombine = { "(%1$s) && (%2$s)", "(%1$s) || (%2$s)", "((%1$s) && (!(%2$s))) || ((!(%1$s)) && (%2$s))", "((%1$s) && (%2$s)) || ((!(%1$s)) && (!(%2$s)))" };

        // yes, oldstyle shaders
        // I would use version 130 or above but there are certain
        // of their new designs I don't agree with. Namely, what's
        // up with removing texture coordinates. That's just plain
        // retarded.

        int success;
        Bmd.Material mat = model.materials[matid];
        shaders[matid] = new Shader();

        StringBuilder vert = new StringBuilder();
        vert.append("#version 120\n");
        vert.append("\n");
        vert.append("void main()\n");
        vert.append("{\n");
        vert.append("    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n");
        vert.append("    gl_FrontColor = gl_Color;\n");
        vert.append("    gl_FrontSecondaryColor = gl_SecondaryColor;\n");
        for (int i = 0; i < mat.numTexgens; i++)
        {
            // TODO matrices
            vert.append(String.format("    gl_TexCoord[%1$d] = %2$s;\n", i, texgensrc[mat.texGen[i].src]));
        }
        vert.append("}\n");

        int vertid = gl.glCreateShader(GL2.GL_VERTEX_SHADER);
        shaders[matid].vertexShader = vertid;
        gl.glShaderSource(vertid, 1, new String[] { vert.toString() }, new int[] { vert.length() }, 0);
        gl.glCompileShader(vertid);
        int[] sillyarray = new int[1];
        gl.glGetShaderiv(vertid, GL2.GL_COMPILE_STATUS, sillyarray, 0);

        success = sillyarray[0];
        if (success == 0)
        {
            //string log = gl.glGetShaderInfoLog(vertid);
            String log = "TODO port this shit from C#";
            System.out.println("ARGH");
            throw new GLException("!Failed to compile vertex shader: " + log);
            // TODO: better error reporting/logging?
        }

        StringBuilder frag = new StringBuilder();
        frag.append("#version 120\n");
        frag.append("\n");

        for (int i = 0; i < 8; i++)
        {
            if (mat.texStages[i] == (short)0xFFFF) continue;
            frag.append(String.format("uniform sampler2D texture%1$d;\n", i));
        }

        frag.append("\n");
        frag.append("float truncc1(float c)\n");
        frag.append("{\n");
        frag.append("    return (c == 0.0) ? 0.0 : ((fract(c) == 0.0) ? 1.0 : fract(c));\n");
        frag.append("}\n");
        frag.append("\n");
        frag.append("vec3 truncc3(vec3 c)\n");
        frag.append("{\n");
        frag.append("    return vec3(truncc1(c.r), truncc1(c.g), truncc1(c.b));\n");
        frag.append("}\n");
        frag.append("\n");
        frag.append("void main()\n");
        frag.append("{\n");

        for (int i = 0; i < 4; i++)
        {
            int _i = (i == 0) ? 3 : i - 1; // ???
            frag.append(String.format(usa, "    vec4 %1$s = vec4(%2$f, %3$f, %4$f, %5$f);\n",
                outputregs[i],
                (float)mat.colorS10[_i].r / 255f, (float)mat.colorS10[_i].g / 255f,
                (float)mat.colorS10[_i].b / 255f, (float)mat.colorS10[_i].a / 255f));
        }

        for (int i = 0; i < 4; i++)
        {
            frag.append(String.format(usa, "    vec4 k%1$d = vec4(%2$f, %3$f, %4$f, %5$f);\n",
                i,
                (float)mat.constColors[i].r / 255f, (float)mat.constColors[i].g / 255f,
                (float)mat.constColors[i].b / 255f, (float)mat.constColors[i].a / 255f));
        }

        frag.append("    vec4 texcolor, rascolor, konst;\n");

        for (int i = 0; i < mat.numTevStages; i++)
        {
            frag.append(String.format("\n    // TEV stage %1$d\n", i));

            // TEV inputs
            // for registers prev/0/1/2: use fract() to emulate truncation
            // if they're selected into a, b or c
            String rout, a, b, c, d, operation;

            frag.append("    konst.rgb = " + c_konstsel[mat.constColorSel[i]] + ";\n");
            frag.append("    konst.a = " + a_konstsel[mat.constAlphaSel[i]] + ";\n");
            if (mat.tevOrder[i].texMap != (byte)0xFF && mat.tevOrder[i].texcoordID != (byte)0xFF)
                frag.append(String.format("    texcolor = texture2D(texture%1$d, gl_TexCoord[%2$d].st);\n",
                    mat.tevOrder[i].texMap, mat.tevOrder[i].texcoordID));
            frag.append("    rascolor = gl_Color;\n");
            // TODO: take mat.TevOrder[i].ChanId into account
            // TODO: tex/ras swizzle? (important or not?)
            //mat.TevSwapMode[0].

            //if (mat.tevOrder[i].chanID != 4)
            //    throw new GLException(String.format("!UNSUPPORTED CHANID %1$d", mat.tevOrder[i].chanID));

            rout = outputregs[mat.tevStage[i].colorRegID] + ".rgb";
            a = c_inputregs[mat.tevStage[i].colorIn[0]];
            b = c_inputregs[mat.tevStage[i].colorIn[1]];
            c = c_inputregs[mat.tevStage[i].colorIn[2]];
            d = c_inputregsD[mat.tevStage[i].colorIn[3]];

            switch (mat.tevStage[i].colorOp)
            {
                case 0:
                    operation = "    %1$s = (%5$s + mix(%2$s,%3$s,%4$s) + vec3(%6$s,%6$s,%6$s)) * vec3(%7$s,%7$s,%7$s);\n";
                    if (mat.tevStage[i].colorClamp != 0) operation += "    %1$s = clamp(%1$s, vec3(0.0,0.0,0.0), vec3(1.0,1.0,1.0));\n";
                    break;

                case 1:
                    operation = "    %1$s = (%5$s - mix(%2$s,%3$s,%4$s) + vec3(%6$s,%6$s,%6$s)) * vec3(%7$s,%7$s,%7$s);\n";
                    if (mat.tevStage[i].colorClamp != 0) operation += "    %1$s = clamp(%1$s, vec3(0.0,0.0,0.0), vec3(1.0,1.0,1.0));\n";
                    break;

                case 8:
                    operation = "    %1$s = %5$s + (((%2$s).r > (%3$s).r) ? %4$s : vec3(0.0,0.0,0.0));\n";
                    break;

                default:
                    operation = "    %1$s = vec3(1.0,0.0,1.0);\n";
                    System.out.println("COLOROP ARGH"); System.out.println(mat.tevStage[i].colorOp);
                    throw new GLException(String.format("!colorop %1$d", mat.tevStage[i].colorOp));
            }

            operation = String.format(operation, 
                rout, a, b, c, d, tevbias[mat.tevStage[i].colorBias],
                tevscale[mat.tevStage[i].colorScale]);
            frag.append(operation);

            rout = outputregs[mat.tevStage[i].alphaRegID] + ".a";
            a = a_inputregs[mat.tevStage[i].alphaIn[0]];
            b = a_inputregs[mat.tevStage[i].alphaIn[1]];
            c = a_inputregs[mat.tevStage[i].alphaIn[2]];
            d = a_inputregsD[mat.tevStage[i].alphaIn[3]];

            switch (mat.tevStage[i].alphaOp)
            {
                case 0:
                    operation = "    %1$s = (%5$s + mix(%2$s,%3$s,%4$s) + %6$s) * %7$s;\n";
                    if (mat.tevStage[i].alphaClamp != 0) operation += "   %1$s = clamp(%1$s, 0.0, 1.0);\n";
                    break;

                case 1:
                    operation = "    %1$s = (%5$s - mix(%2$s,%3$s,%4$s) + %6$s) * %7$s;\n";
                    if (mat.tevStage[i].alphaClamp != 0) operation += "   %1$s = clamp(%1$s, 0.0, 1.0);\n";
                    break;

                default:
                    operation = "    %1$s = 1.0;";
                    System.out.println("ALPHAOP ARGH"); System.out.println(mat.tevStage[i].alphaOp);
                    throw new GLException(String.format("!alphaop %1$d", mat.tevStage[i].alphaOp));
            }

            operation = String.format(operation,
                rout, a, b, c, d, tevbias[mat.tevStage[i].alphaBias],
                tevscale[mat.tevStage[i].alphaScale]);
            frag.append(operation);
        }

        frag.append("\n");
        frag.append("   gl_FragColor.rgb = truncc3(rprev.rgb);\n");
        frag.append("   gl_FragColor.a = truncc1(rprev.a);\n");
        frag.append("\n");

        frag.append("    // Alpha test\n");
        if (mat.alphaComp.mergeFunc == 1 && (mat.alphaComp.func0 == 7 || mat.alphaComp.func1 == 7))
        {
            // always pass -- do nothing :)
        }
        else if (mat.alphaComp.mergeFunc == 0 && (mat.alphaComp.func0 == 0 || mat.alphaComp.func1 == 0))
        {
            // never pass
            // (we did all those color/alpha calculations for uh, nothing ;_; )
            frag.append("    discard;\n");
        }
        else
        {
            String compare0 = String.format(usa, alphacompare[mat.alphaComp.func0], "gl_FragColor.a", (float)mat.alphaComp.ref0 / 255f);
            String compare1 = String.format(usa, alphacompare[mat.alphaComp.func1], "gl_FragColor.a", (float)mat.alphaComp.ref1 / 255f);
            String fullcompare = "";

            if (mat.alphaComp.mergeFunc == 1)
            {
                if (mat.alphaComp.func0 == 0) fullcompare = compare1;
                else if (mat.alphaComp.func1 == 0) fullcompare = compare0;
            }
            else if (mat.alphaComp.mergeFunc == 0)
            {
                if (mat.alphaComp.func0 == 7) fullcompare = compare1;
                else if (mat.alphaComp.func1 == 7) fullcompare = compare0;
            }

            if (fullcompare == "") fullcompare = String.format(alphacombine[mat.alphaComp.mergeFunc], compare0, compare1);

            frag.append("    if (!(" + fullcompare + ")) discard;\n");
        }

        frag.append("}\n");

        int fragid = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);
        shaders[matid].fragmentShader = fragid;
        String lol = frag.toString();    
        gl.glShaderSource(fragid, 1, new String[] { frag.toString() }, new int[] { frag.length()}, 0);
        gl.glCompileShader(fragid);

        gl.glGetShaderiv(fragid, GL2.GL_COMPILE_STATUS, sillyarray, 0);
        success = sillyarray[0];
        if (success == 0)
        {
            //string log = gl.glGetShaderInfoLog(fragid);
            gl.glGetShaderiv(fragid, GL2.GL_INFO_LOG_LENGTH, sillyarray, 0);
            int loglength = sillyarray[0];
            byte[] _log = new byte[loglength];
            gl.glGetShaderInfoLog(fragid, loglength, sillyarray, 0, _log, 0);
            CharBuffer log;
            try { log = Charset.forName("ASCII").newDecoder().decode(ByteBuffer.wrap(_log)); } catch (Exception ex) { log = CharBuffer.wrap("lolfail"); }
            throw new GLException("!Failed to compile fragment shader: " + log.toString() + "\n" + frag.toString());
            // TODO: better error reporting/logging?
        }

        int sid = gl.glCreateProgram();
        shaders[matid].program = sid;

        gl.glAttachShader(sid, vertid);
        gl.glAttachShader(sid, fragid);

        gl.glLinkProgram(sid);
        gl.glGetProgramiv(sid, GL2.GL_LINK_STATUS, sillyarray, 0);
        success = sillyarray[0];
        if (success == 0)
        {
            //string log = gl.glGetProgramInfoLog(sid);
            String log = "TODO: port this shit from C#";
            throw new GLException("!Failed to link shader program: " + log);
            // TODO: better error reporting/logging?
        }
    }


    public BmdRenderer(RenderInfo info, Bmd model) throws GLException
    {
        GL2 gl = info.drawable.getGL().getGL2();
        this.model = model;

        String extensions = gl.glGetString(GL2.GL_EXTENSIONS);
        hasShaders = extensions.contains("GL_ARB_shading_language_100") &&
            extensions.contains("GL_ARB_shader_objects") &&
            extensions.contains("GL_ARB_vertex_shader") &&
            extensions.contains("GL_ARB_fragment_shader");
        hasShaders = hasShaders && Whitehole.useShaders;

        textures = new int[model.textures.length];
        for (int i = 0; i < model.textures.length; i++)
            uploadTexture(gl, i);

        if (hasShaders)
        {
            shaders = new Shader[model.materials.length];
            for (int i = 0; i < model.materials.length; i++)
            {
                try { generateShaders(gl, i); }
                catch (GLException ex)
                {
                    // really ugly hack
                    if (ex.getMessage().charAt(0) == '!')
                    {
                        //StringBuilder src = new StringBuilder(10000); int lolz;
                        //gl.glGetShaderSource(shaders[i].FragmentShader, 10000, out lolz, src);
                        //System.Windows.Forms.MessageBox.Show(ex.Message + "\n" + src.ToString());
                        throw ex;
                    }

                    shaders[i].program = 0;
                }
            }
        }
    }

    @Override
    public void close(RenderInfo info) throws GLException
    {
        GL2 gl = info.drawable.getGL().getGL2();
        
        if (hasShaders)
        {
            for (Shader shader : shaders)
            {
                if (shader.vertexShader > 0)
                {
                    gl.glDetachShader(shader.program, shader.vertexShader);
                    gl.glDeleteShader(shader.vertexShader);
                }

                if (shader.fragmentShader > 0)
                {
                    gl.glDetachShader(shader.program, shader.fragmentShader);
                    gl.glDeleteShader(shader.fragmentShader);
                }

                if (shader.program > 0)
                    gl.glDeleteProgram(shader.program);
            }
        }

        for (int tex : textures)
            gl.glDeleteTextures(1, new int[] { tex }, 0);

        try { model.close(); }
        catch (IOException ex) { }
    }

    @Override
    public Boolean gottaRender(RenderInfo info) throws GLException
    {
        for (Bmd.Material mat : model.materials)
        {
            if (!((mat.drawFlag == 4) ^ (info.renderMode == RenderMode.TRANSLUCENT)))
                return true;
        }

        return false;
    }

    @Override
    public void render(RenderInfo info) throws GLException
    {
        GL2 gl = info.drawable.getGL().getGL2();
        
        int[] blendsrc = { GL2.GL_ZERO, GL2.GL_ONE,
                           GL2.GL_ONE, GL2.GL_ZERO, // um...
                           GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA, 
                           GL2.GL_DST_ALPHA, GL2.GL_ONE_MINUS_DST_ALPHA,
                           GL2.GL_DST_COLOR, GL2.GL_ONE_MINUS_DST_COLOR };
        int[] blenddst = { GL2.GL_ZERO, GL2.GL_ONE,
                           GL2.GL_SRC_COLOR, GL2.GL_ONE_MINUS_SRC_COLOR,
                           GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA, 
                           GL2.GL_DST_ALPHA, GL2.GL_ONE_MINUS_DST_ALPHA,
                           GL2.GL_DST_COLOR, GL2.GL_ONE_MINUS_DST_COLOR };
        int[] logicop = { GL2.GL_CLEAR, GL2.GL_AND, GL2.GL_AND_REVERSE, GL2.GL_COPY,
                          GL2.GL_AND_INVERTED, GL2.GL_NOOP, GL2.GL_XOR, GL2.GL_OR,
                          GL2.GL_NOR, GL2.GL_EQUIV, GL2.GL_INVERT, GL2.GL_OR_REVERSE,
                          GL2.GL_COPY_INVERTED, GL2.GL_OR_INVERTED, GL2.GL_NAND, GL2.GL_SET };

        Matrix4[] lastmatrixtable = null;

        for (Bmd.SceneGraphNode node : model.sceneGraph)
        {
            if (node.nodeType != 0) continue;
            int shape = node.nodeID;

            if (node.materialID != 0xFFFF)
            {
                int[] cullmodes = { GL2.GL_FRONT, GL2.GL_BACK, GL2.GL_FRONT_AND_BACK };
                int[] depthfuncs = { GL2.GL_NEVER, GL2.GL_LESS, GL2.GL_EQUAL, GL2.GL_LEQUAL,
                                     GL2.GL_GREATER, GL2.GL_NOTEQUAL, GL2.GL_GEQUAL, GL2.GL_ALWAYS };

                Bmd.Material mat = model.materials[node.materialID];

                if ((mat.drawFlag == 4) ^ (info.renderMode == RenderMode.TRANSLUCENT))
                    continue;

                if (hasShaders)
                {
                    // shader: handles multitexturing, color combination, alpha test
                    gl.glUseProgram(shaders[node.materialID].program);

                    // do multitexturing
                    for (int i = 0; i < 8; i++)
                    {
                        gl.glActiveTexture(GL2.GL_TEXTURE0 + i);

                        if (mat.texStages[i] == (short)0xFFFF)
                        {
                            gl.glDisable(GL2.GL_TEXTURE_2D);
                            continue;
                        }

                        int loc = gl.glGetUniformLocation(shaders[node.materialID].program, String.format("texture%1$d", i));
                        gl.glUniform1i(loc, i);

                        int texid = textures[mat.texStages[i]];
                        gl.glEnable(GL2.GL_TEXTURE_2D);
                        gl.glBindTexture(GL2.GL_TEXTURE_2D, texid);
                    }
                }
                else
                {
                    int[] alphafunc = { GL2.GL_NEVER, GL2.GL_LESS, GL2.GL_EQUAL, GL2.GL_LEQUAL,
                                        GL2.GL_GREATER, GL2.GL_NOTEQUAL, GL2.GL_GEQUAL, GL2.GL_ALWAYS };

                    // texturing -- texture 0 will be used
                    if (mat.texStages[0] != (short)0xFFFF)
                    {
                        int texid = textures[mat.texStages[0]];
                        gl.glEnable(GL2.GL_TEXTURE_2D);
                        gl.glBindTexture(GL2.GL_TEXTURE_2D, texid);
                    }
                    else
                        gl.glDisable(GL2.GL_TEXTURE_2D);

                    // alpha test -- only one comparison can be done
                    if (mat.alphaComp.mergeFunc == 1 && (mat.alphaComp.func0 == 7 || mat.alphaComp.func1 == 7))
                        gl.glDisable(GL2.GL_ALPHA_TEST);
                    else if (mat.alphaComp.mergeFunc == 0 && (mat.alphaComp.func0 == 0 || mat.alphaComp.func1 == 0))
                    {
                        gl.glEnable(GL2.GL_ALPHA_TEST);
                        gl.glAlphaFunc(GL2.GL_NEVER, 0f);
                    }
                    else
                    {
                        gl.glEnable(GL2.GL_ALPHA_TEST);

                        if ((mat.alphaComp.mergeFunc == 1 && mat.alphaComp.func0 == 0) || (mat.alphaComp.mergeFunc == 0 && mat.alphaComp.func0 == 7))
                            gl.glAlphaFunc(alphafunc[mat.alphaComp.func1], (float)mat.alphaComp.ref1 / 255f);
                        else
                            gl.glAlphaFunc(alphafunc[mat.alphaComp.func0], (float)mat.alphaComp.ref0 / 255f);
                    }
                }

                switch (mat.blendMode.blendMode)
                {
                    case 0: 
                        gl.glDisable(GL2.GL_BLEND);
                        gl.glDisable(GL2.GL_COLOR_LOGIC_OP);
                        break;

                    case 1:
                    case 3:
                        gl.glEnable(GL2.GL_BLEND);
                        gl.glDisable(GL2.GL_COLOR_LOGIC_OP);

                        if (mat.blendMode.blendMode == 3)
                            gl.glBlendEquation(GL2.GL_FUNC_SUBTRACT);
                        else
                            gl.glBlendEquation(GL2.GL_FUNC_ADD);

                        gl.glBlendFunc(blendsrc[mat.blendMode.srcFactor], blenddst[mat.blendMode.dstFactor]);
                        break;

                    case 2:
                        gl.glDisable(GL2.GL_BLEND);
                        gl.glEnable(GL2.GL_COLOR_LOGIC_OP);
                        gl.glLogicOp(logicop[mat.blendMode.blendOp]);
                        break;
                }


                if (mat.cullMode == 0)
                    gl.glDisable(GL2.GL_CULL_FACE);
                else
                {
                    gl.glEnable(GL2.GL_CULL_FACE);
                    gl.glCullFace(cullmodes[mat.cullMode - 1]);
                }

                if (mat.zMode.enableZTest)
                {
                    gl.glEnable(GL2.GL_DEPTH_TEST);
                    gl.glDepthFunc(depthfuncs[mat.zMode.func]);
                }
                else
                    gl.glDisable(GL2.GL_DEPTH_TEST);

                gl.glDepthMask(mat.zMode.enableZWrite);
            }
            else
            {
                //if (info.Mode != RenderMode.Opaque) continue;
                // if (m_HasShaders) gl.glUseProgram(0);
                throw new GLException(String.format("Material-less geometry node %1$d", node.nodeID));
            }


            Bmd.Batch batch = model.batches[shape];

            /*if (batch.MatrixType == 1)
            {
                gl.glPushMatrix();
                gl.glCallList(info.BillboardDL);
            }
            else if (batch.MatrixType == 2)
            {
                gl.glPushMatrix();
                gl.glCallList(info.YBillboardDL);
            }*/

            for (Bmd.Batch.Packet packet : batch.packets)
            {
                Matrix4[] mtxtable = new Matrix4[packet.matrixTable.length];
                int[] mtx_debug = new int[packet.matrixTable.length];

                for (int i = 0; i < packet.matrixTable.length; i++)
                {
                    if (packet.matrixTable[i] == (short)0xFFFF)
                    {
                        mtxtable[i] = lastmatrixtable[i];
                        mtx_debug[i] = 2;
                    }
                    else
                    {
                        Bmd.MatrixType mtxtype = model.matrixTypes[packet.matrixTable[i]];

                        if (mtxtype.isWeighted)
                        {
                            //throw new NotImplementedException("weighted matrix");

                            // code inspired from bmdview2, except doesn't work right
                            /*Matrix4 mtx = new Matrix4();
                            Bmd.MultiMatrix mm = m_Model.MultiMatrices[mtxtype.Index];
                            for (int j = 0; j < mm.NumMatrices; j++)
                            {
                                Matrix4 wmtx = mm.Matrices[j];
                                float weight = mm.MatrixWeights[j];

                                Matrix4.Mult(ref wmtx, ref m_Model.Joints[mm.MatrixIndices[j]].Matrix, out wmtx);

                                Vector4.Mult(ref wmtx.Row0, weight, out wmtx.Row0);
                                Vector4.Mult(ref wmtx.Row1, weight, out wmtx.Row1);
                                Vector4.Mult(ref wmtx.Row2, weight, out wmtx.Row2);
                                //Vector4.Mult(ref wmtx.Row3, weight, out wmtx.Row3);

                                Vector4.Add(ref mtx.Row0, ref wmtx.Row0, out mtx.Row0);
                                Vector4.Add(ref mtx.Row1, ref wmtx.Row1, out mtx.Row1);
                                Vector4.Add(ref mtx.Row2, ref wmtx.Row2, out mtx.Row2);
                                //Vector4.Add(ref mtx.Row3, ref wmtx.Row3, out mtx.Row3);
                            }
                            mtx.M44 = 1f;
                            mtxtable[i] = mtx;*/

                            // seems fine in most cases
                            // but hey, certainly not right, that data has to be used in some way
                            mtxtable[i] = new Matrix4();

                            mtx_debug[i] = 1;
                        }
                        else
                        {
                            mtxtable[i] = model.joints[mtxtype.index].finalMatrix;
                            mtx_debug[i] = 0;
                        }
                    }
                }

                lastmatrixtable = mtxtable;

                for (Bmd.Batch.Packet.Primitive prim : packet.primitives)
                {
                    int[] primtypes = { GL2.GL_QUADS, GL2.GL_POINTS, GL2.GL_TRIANGLES, GL2.GL_TRIANGLE_STRIP,
                                        GL2.GL_TRIANGLE_FAN, GL2.GL_LINES, GL2.GL_LINE_STRIP, GL2.GL_POINTS };
                    gl.glBegin(primtypes[(prim.primitiveType - 0x80) / 8]);
                    //gl.glBegin(BeginMode.Points);

                    for (int i = 0; i < prim.numIndices; i++)
                    {

                        if ((prim.arrayMask & (1 << 11)) != 0) { Color4 c = model.colorArray[0][prim.colorIndices[0][i]]; gl.glColor4f(c.r, c.g, c.b, c.a); }

                        if (hasShaders)
                        {
                            if ((prim.arrayMask & (1 << 12)) != 0) { Color4 c = model.colorArray[1][prim.colorIndices[1][i]]; gl.glSecondaryColor3f(c.r, c.g, c.b); }

                            if ((prim.arrayMask & (1 << 13)) != 0) { Vector2 t = model.texcoordArray[0][prim.texcoordIndices[0][i]]; gl.glMultiTexCoord2f(GL2.GL_TEXTURE0, t.x, t.y); }
                            if ((prim.arrayMask & (1 << 14)) != 0) { Vector2 t = model.texcoordArray[1][prim.texcoordIndices[1][i]]; gl.glMultiTexCoord2f(GL2.GL_TEXTURE1, t.x, t.y); }
                            if ((prim.arrayMask & (1 << 15)) != 0) { Vector2 t = model.texcoordArray[2][prim.texcoordIndices[2][i]]; gl.glMultiTexCoord2f(GL2.GL_TEXTURE2, t.x, t.y); }
                            if ((prim.arrayMask & (1 << 16)) != 0) { Vector2 t = model.texcoordArray[3][prim.texcoordIndices[3][i]]; gl.glMultiTexCoord2f(GL2.GL_TEXTURE3, t.x, t.y); }
                            if ((prim.arrayMask & (1 << 17)) != 0) { Vector2 t = model.texcoordArray[4][prim.texcoordIndices[4][i]]; gl.glMultiTexCoord2f(GL2.GL_TEXTURE4, t.x, t.y); }
                            if ((prim.arrayMask & (1 << 18)) != 0) { Vector2 t = model.texcoordArray[5][prim.texcoordIndices[5][i]]; gl.glMultiTexCoord2f(GL2.GL_TEXTURE5, t.x, t.y); }
                            if ((prim.arrayMask & (1 << 19)) != 0) { Vector2 t = model.texcoordArray[6][prim.texcoordIndices[6][i]]; gl.glMultiTexCoord2f(GL2.GL_TEXTURE6, t.x, t.y); }
                            if ((prim.arrayMask & (1 << 20)) != 0) { Vector2 t = model.texcoordArray[7][prim.texcoordIndices[7][i]]; gl.glMultiTexCoord2f(GL2.GL_TEXTURE7, t.x, t.y); }
                        }
                        else
                        {
                            if ((prim.arrayMask & (1 << 13)) != 0) { Vector2 t = model.texcoordArray[0][prim.texcoordIndices[0][i]]; gl.glTexCoord2f(t.x, t.y); }
                        }
                        //if ((prim.ArrayMask & (1 << 0)) != 0) gl.glColor4(debug[prim.PosMatrixIndices[i]]);

                        if ((prim.arrayMask & (1 << 10)) != 0) { Vector3 n = model.normalArray[prim.normalIndices[i]]; gl.glNormal3f(n.x, n.y, n.z); }

                        Vector3 pos = new Vector3(model.positionArray[prim.positionIndices[i]]);
                        if ((prim.arrayMask & 1) != 0) Vector3.transform(pos, mtxtable[prim.posMatrixIndices[i]], pos);
                        else Vector3.transform(pos, mtxtable[0], pos);
                        gl.glVertex3f(pos.x, pos.y, pos.z);
                    }

                    gl.glEnd();
                }
            }

            //if (batch.MatrixType == 1 || batch.MatrixType == 2)
            //     gl.glPopMatrix();
        }
    }


    private class Shader
    {
        public int program, vertexShader, fragmentShader;
    }

    private Bmd model;
    private int[] textures;
    private Boolean hasShaders;
    private Shader[] shaders;
}
