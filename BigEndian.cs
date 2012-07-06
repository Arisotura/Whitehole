using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace Whitehole
{
    public class BinaryReaderBE : BinaryReader
    {
        public BinaryReaderBE(Stream s)
            : base(s)
        { }

        public BinaryReaderBE(Stream s, Encoding e)
            : base(s, e)
        { }


        public override short ReadInt16()
        {
            UInt16 val = base.ReadUInt16();
            return (Int16)((val >> 8) | (val << 8));
        }

        public override int ReadInt32()
        {
            UInt32 val = base.ReadUInt32();
            return (Int32)((val >> 24) | ((val & 0xFF0000) >> 8) | ((val & 0xFF00) << 8) | (val << 24));
        }


        public override ushort ReadUInt16()
        {
            UInt16 val = base.ReadUInt16();
            return (UInt16)((val >> 8) | (val << 8));
        }

        public override uint ReadUInt32()
        {
            UInt32 val = base.ReadUInt32();
            return (UInt32)((val >> 24) | ((val & 0xFF0000) >> 8) | ((val & 0xFF00) << 8) | (val << 24));
        }


        public override float ReadSingle()
        {
            byte[] stuff = base.ReadBytes(4);
            if (BitConverter.IsLittleEndian) Array.Reverse(stuff);
            float val = BitConverter.ToSingle(stuff, 0);
            return val;
        }

        public override double ReadDouble()
        {
            byte[] stuff = base.ReadBytes(8);
            if (BitConverter.IsLittleEndian) Array.Reverse(stuff);
            double val = BitConverter.ToDouble(stuff, 0);
            return val;
        }
    }


    public class BinaryWriterBE : BinaryWriter
    {
        public BinaryWriterBE(Stream s)
            : base(s)
        { }

        public BinaryWriterBE(Stream s, Encoding e)
            : base(s, e)
        { }


        public override void Write(short value)
        {
            ushort val = (ushort)value;
            base.Write((short)((val >> 8) | (val << 8)));
        }

        public override void Write(int value)
        {
            uint val = (uint)value;
            base.Write((int)((val >> 24) | ((val & 0xFF0000) >> 8) | ((val & 0xFF00) << 8) | (val << 24)));
        }


        public override void Write(ushort value)
        {
            base.Write((ushort)((value >> 8) | (value << 8)));
        }

        public override void Write(uint value)
        {
            base.Write((uint)((value >> 24) | ((value & 0xFF0000) >> 8) | ((value & 0xFF00) << 8) | (value << 24)));
        }


        public override void Write(float value)
        {
            byte[] stuff = BitConverter.GetBytes(value);
            if (BitConverter.IsLittleEndian) Array.Reverse(stuff);
            base.Write(stuff);
        }

        public override void Write(double value)
        {
            byte[] stuff = BitConverter.GetBytes(value);
            if (BitConverter.IsLittleEndian) Array.Reverse(stuff);
            base.Write(stuff);
        }
    }
}
