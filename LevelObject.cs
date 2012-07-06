using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ComponentModel;
using OpenTK;

namespace Whitehole
{
    public class LoltestPropertyDescriptor : PropertyDescriptor
    {
        public LoltestPropertyDescriptor()
            : base("warp!", new Attribute[] {})
        {
        }

        public override string DisplayName
        {
            get
            {
                return "awooga";
            }
        }

        public override bool ShouldSerializeValue(object component)
        {
            return false;
        }

        public override void SetValue(object component, object value)
        {
            //
        }

        public override void ResetValue(object component)
        {
            //
        }

        public override object GetValue(object component)
        {
            return 1338;
        }

        public override bool CanResetValue(object component)
        {
            return false;
        }

        public override Type PropertyType
        {
            get { return typeof(int); }
        }

        public override bool IsReadOnly
        {
            get { return false; }
        }

        public override Type ComponentType
        {
            get { return typeof(int); }
        }
    }

    public class LoltestConverter : TypeConverter
    {
        public override bool GetPropertiesSupported(ITypeDescriptorContext context)
        {
            return true;
        }

        public override PropertyDescriptorCollection GetProperties(ITypeDescriptorContext context, object _value, Attribute[] attributes)
        {
            Dictionary<string, object> value = (Dictionary<string, object>)_value;
            PropertyDescriptor[] props = new PropertyDescriptor[value.Count];

            int i = 0;
            foreach (KeyValuePair<string, object> entry in value)
            {
                props[i++] = new LoltestPropertyDescriptor();
            }
            
            return new PropertyDescriptorCollection(props);
        }
    }

    public class VectorConverter : TypeConverter
    {
        public override bool GetPropertiesSupported(ITypeDescriptorContext context)
        {
            return true;
        }

        public override PropertyDescriptorCollection GetProperties(ITypeDescriptorContext context, object _value, Attribute[] attributes)
        {
            Vector3 value = (Vector3)_value;
            PropertyDescriptor[] props = new PropertyDescriptor[3];

            for (int i = 0;i < 3; i++)
            {
                props[i++] = new LoltestPropertyDescriptor();
            }

            return new PropertyDescriptorCollection(props);
        }
    }


    public class LevelObjectBase
    {
        public LevelObjectBase(Bcsv.Entry entry)
        {
            BcsvEntry = entry;
            Model = ModelCache.GetObjectModel(this);
        }

        public object this[string key]
        {
            get
            {
                return this.BcsvEntry[key];
            }

            set
            {
                this.BcsvEntry[key] = value;
            }
        }

        public override string ToString()
        {
            return "LevelObjectBase";
        }
        

        public Bcsv.Entry BcsvEntry;
        public ModelCache.Entry Model;

        public Dictionary<string, object> ExtraAttribs;
    }


    public class LevelObject : LevelObjectBase
    {
        public LevelObject(Bcsv.Entry entry)
            : base(entry)
        {
        }

        public override string ToString()
        {
            return string.Format("[{0}] {1}", BcsvEntry["l_id"], BcsvEntry["name"]);
        }


        [Category("General"), DisplayName("Name"), Description("What the object will be.")]
        public string Name
        {
            get { return (string)BcsvEntry["name"]; }
            set { BcsvEntry["name"] = value; }
        }
        [Category("General"), DisplayName("X Position"), Description("The position of the object along the X axis.")]
        public float XPosition
        {
            get { return (float)BcsvEntry["pos_x"]; }
            set { BcsvEntry["pos_x"] = value; }
        }
        [Category("General"), DisplayName("Y Position"), Description("The position of the object along the Y axis.")]
        public float YPosition
        {
            get { return (float)BcsvEntry["pos_y"]; }
            set { BcsvEntry["pos_y"] = value; }
        }
        [Category("General"), DisplayName("Z Position"), Description("The position of the object along the Z axis.")]
        public float ZPosition
        {
            get { return (float)BcsvEntry["pos_z"]; }
            set { BcsvEntry["pos_z"] = value; }
        }

        [Category("General"), DisplayName("Extra"), Description("lolol"), TypeConverter(typeof(LoltestConverter))]
        public Dictionary<string, object> ExtraLolz
        {
            get { return ExtraAttribs; }
        }

        [Category("General"), DisplayName("Vector test"), Description("not interesting"), TypeConverter(typeof(VectorConverter))]
        public Vector3 VectorTest
        {
            get { return new Vector3(1f, 33f, 7f); }
        }
    }
}
