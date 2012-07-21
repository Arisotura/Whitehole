package whitehole;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import java.nio.*;
import java.nio.charset.*;
import javax.swing.JLabel;

public class ObjectDBUpdater extends Thread
{
    public ObjectDBUpdater(JLabel statuslabel)
    {
        m_StatusLabel = statuslabel;
    }
    
    @Override
    public void run()
    {
        try
        {
            String ts = String.format("&ts=%1$d", ObjectDB.Timestamp);
            URL url = new URL(Whitehole.WebsiteURL + "whitehole/objectdb.php?whitehole" + ts);
            URLConnection conn = url.openConnection();
            DataInputStream dis = new DataInputStream(conn.getInputStream());
            
            int length = conn.getContentLength();
            if (length < 8)
            {
                m_StatusLabel.setText("Failed to update object database: received invalid data.");
                return;
            }
            
            byte[] data = new byte[length];
            for (int i = 0; i < data.length; i++)
                data[i] = dis.readByte();
            
            Charset charset = Charset.forName("UTF-8");
            CharsetDecoder dec = charset.newDecoder();
            String strdata = dec.decode(ByteBuffer.wrap(data, 0, 8)).toString();
            
            if (strdata.equals("noupdate"))
            {
                m_StatusLabel.setText("Object database already up-to-date.");
                return;
            }
            
            CRC32 crc = new CRC32();
            crc.update(data, 9, data.length-9);
            long crcref = Long.parseLong(strdata, 16);
            if (crc.getValue() != crcref)
            {
                m_StatusLabel.setText("Failed to update object database: received invalid data.");
                return;
            }
            
            File odbbkp = new File("objectdb.xml.bak");
            File odb = new File("objectdb.xml");
            odb.renameTo(odbbkp);
            try
            {
                odb.delete();
                odb.createNewFile();
                FileOutputStream odbstream = new FileOutputStream(odb);
                odbstream.write(data, 9, data.length-9);
                odbstream.flush();
                odbstream.close();
                odbbkp.delete();
            }
            catch (IOException ex)
            {
                m_StatusLabel.setText("Failed to save new object database.");
                odbbkp.renameTo(odb);
                return;
            }
            
            m_StatusLabel.setText("Object database updated.");
        }
        catch (MalformedURLException ex)
        {
            m_StatusLabel.setText("Failed to connect to update server.");
        }
        catch (IOException ex)
        {
            m_StatusLabel.setText("Failed to save new object database.");
        }
    }
    
    
    // 0 == success
    // 1 == error while connecting to server
    // 2 == integrity check failed
    // 3 == error writing downloaded ObjectDB to HDD
    // 4 == already up-to-date
    private JLabel m_StatusLabel;
}
