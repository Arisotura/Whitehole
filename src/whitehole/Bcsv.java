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

package whitehole;

import java.io.*;
import java.util.*;
import whitehole.fileio.*;

public class Bcsv 
{
    public Bcsv(FileBase file) throws IOException
    {
        m_File = file;
        m_File.SetBigEndian(true);

        m_File.Position(0);
        int entrycount = m_File.ReadInt();
        int fieldcount = m_File.ReadInt();
        int dataoffset = m_File.ReadInt();
        int entrydatasize = m_File.ReadInt();
        
        Fields = new HashMap<>(fieldcount);
        Entries = new ArrayList<>(entrycount);

        int stringtableoffset = (int)(dataoffset + (entrycount * entrydatasize));

        for (int i = 0; i < fieldcount; i++)
        {
            Field field = new Field();
            m_File.Position(0x10 + (0xC * i));

            field.NameHash = m_File.ReadInt();
            field.Mask = m_File.ReadInt();
            field.EntryOffset = m_File.ReadShort();
            field.ShiftAmount = m_File.ReadByte();
            field.Type = m_File.ReadByte();

            String fieldname = Bcsv.HashToFieldName(field.NameHash);
            field.Name = fieldname;
            Fields.put(field.NameHash, field);
        }

        for (int i = 0; i < entrycount; i++)
        {
            Entry entry = new Entry();

            for (Field field: Fields.values())
            {
                m_File.Position(dataoffset + (i * entrydatasize) + field.EntryOffset);

                Object val = null;
                switch (field.Type)
                {
                    case 0:
                    case 3:
                        val = (int)((m_File.ReadInt() & field.Mask) >>> field.ShiftAmount);
                        break;

                    case 4:
                        val = (short)((m_File.ReadShort() & field.Mask) >>> field.ShiftAmount);
                        break;

                    case 5:
                        val = (byte)((m_File.ReadByte() & field.Mask) >>> field.ShiftAmount);
                        break;

                    case 2:
                        val = m_File.ReadFloat();
                        break;

                    case 6:
                        int str_offset = m_File.ReadInt();
                        m_File.Position(stringtableoffset + str_offset);
                        val = m_File.ReadString("SJIS", 0);
                        break;

                    default:
                        throw new IOException(String.format("Bcsv: unsupported data type %1$02X", field.Type));
                }

                entry.put(field.NameHash, val);
            }

            Entries.add(entry);
        }
    }

    public void Save() throws IOException
    {
        int[] datasizes = { 4, -1, 4, 4, 2, 1, 4 };
        int entrysize = 0;

        for (Field field : Fields.values())
        {
            short fieldend = (short)(field.EntryOffset + datasizes[field.Type]);
            if (fieldend > entrysize) entrysize = fieldend;
        }

        int dataoffset = (int)(0x10 + (0xC * Fields.size()));
        int stringtableoffset = (int)(dataoffset + (Entries.size() * entrysize));
        int curstring = 0;

        m_File.SetLength(stringtableoffset);

        m_File.Position(0);
        m_File.WriteInt(Entries.size());
        m_File.WriteInt(Fields.size());
        m_File.WriteInt(dataoffset);
        m_File.WriteInt(entrysize);

        for (Field field : Fields.values())
        {
            m_File.WriteInt(field.NameHash);
            m_File.WriteInt(field.Mask);
            m_File.WriteShort(field.EntryOffset);
            m_File.WriteByte(field.ShiftAmount);
            m_File.WriteByte(field.Type);
        }

        int i = 0;
        HashMap<String, Integer> stringoffsets = new HashMap<>();

        for (Entry entry : Entries)
        {
            for (Field field : Fields.values())
            {
                int valoffset = (int)(dataoffset + (i * entrysize) + field.EntryOffset);
                m_File.Position(valoffset);

                switch (field.Type)
                {
                    case 0:
                    case 3:
                        {
                            int val = m_File.ReadInt();
                            val &= ~field.Mask;
                            val |= (((int)entry.get(field.NameHash) << field.ShiftAmount) & field.Mask);

                            m_File.Position(valoffset);
                            m_File.WriteInt(val);
                        }
                        break;

                    case 4:
                        {
                            short val = m_File.ReadShort();
                            val &= (short)(~field.Mask);
                            val |= (short)(((short)entry.get(field.NameHash) << field.ShiftAmount) & field.Mask);

                            m_File.Position(valoffset);
                            m_File.WriteShort(val);
                        }
                        break;

                    case 5:
                        {
                            byte val = m_File.ReadByte();
                            val &= (byte)(~field.Mask);
                            val |= (byte)(((byte)entry.get(field.NameHash) << field.ShiftAmount) & field.Mask);

                            m_File.Position(valoffset);
                            m_File.WriteByte(val);
                        }
                        break;

                    case 2:
                        m_File.WriteFloat((float)entry.get(field.NameHash));
                        break;

                    case 6:
                        {
                            String val = (String)entry.get(field.NameHash);
                            if (stringoffsets.containsKey(val))
                                m_File.WriteInt(stringoffsets.get(val));
                            else
                            {
                                stringoffsets.put(val, curstring);
                                m_File.WriteInt(curstring);
                                m_File.Position(stringtableoffset + curstring);
                                curstring += m_File.WriteString("SJIS", val, 0);
                            }
                        }
                        break;
                }
            }

            i++;
        }

        m_File.Save();
    }

    public void Close() throws IOException
    {
        m_File.Close();
    }


    public Field AddField(String name, int offset, byte type, int mask, int shift, Object defaultval)
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
            for (Field field : Fields.values())
            {
                short fieldend = (short)(field.EntryOffset + datasizes[field.Type]);
                if (fieldend > offset) offset = fieldend;
            }
        }

        Field newfield = new Field();
        newfield.Name = name;
        newfield.NameHash = Bcsv.FieldNameToHash(name);
        newfield.Mask = mask;
        newfield.ShiftAmount = (byte)shift;
        newfield.Type = type;
        newfield.EntryOffset = (short)offset;
        Fields.put(newfield.NameHash, newfield);

        for (Entry entry : Entries)
        {
            entry.put(name, defaultval);
        }

        return newfield;
    }

    public void RemoveField(String name)
    {
        int hash = Bcsv.FieldNameToHash(name);
        Fields.remove(hash);

        for (Entry entry : Entries)
        {
            entry.remove(hash);
        }
    }


    public class Field
    {
        public int NameHash;
        public int Mask;
        public short EntryOffset;
        public byte ShiftAmount;
        public byte Type;

        public String Name;
    }

    public class Entry extends HashMap<Integer, Object>
    {
        public Entry()
        { super(); }

        public Object get(String key)
        {
            return get(Bcsv.FieldNameToHash(key));
        }
        
        public void put(String key, Object val)
        {
            put(Bcsv.FieldNameToHash(key), val);
        }

        public Boolean containsKey(String key)
        {
            return this.containsKey(Bcsv.FieldNameToHash(key));
        }
    }


    private FileBase m_File;

    public HashMap<Integer, Field> Fields;
    public List<Entry> Entries;


    // Field name hash support functions
    // the hash->String table is meant for debugging purposes and
    // shouldn't be used by proper code

    public static int FieldNameToHash(String field)
    {
        int ret = 0;
        for (char ch : field.toCharArray())
        {
            ret *= 0x1F;
            ret += ch;
        }
        return ret;
    }

    public static String HashToFieldName(int hash)
    {
        if (!m_HashTable.containsKey(hash))
            return String.format("[%1$X8]", hash);

        return m_HashTable.get(hash);
    }

    public static void AddHash(String field)
    {
        int hash = FieldNameToHash(field);
        if (!m_HashTable.containsKey(hash))
            m_HashTable.put(hash, field);
    }

    public static void PopulateHashtable()
    {
        m_HashTable = new HashMap<>();

        try
        {
            InputStream strm = Whitehole.class.getResourceAsStream("/Resources/KnownFieldNames.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(strm));

            String line;
            while ((line = reader.readLine()) != null)
            {
                line = line.trim();

                if (line.length() == 0) continue;
                if (line.charAt(0) == '#') continue;

                AddHash(line);
            }
        }
        catch (IOException ex) {}
    }

    public static HashMap<Integer, String> m_HashTable; 
}
