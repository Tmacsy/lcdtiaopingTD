package com.rong.lcdbusview.link;

import com.rong.lcdbusview.tools.LogTools;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class routenameinfo {

    private String linename;//线路名称


    public routenameinfo(byte[] buffer) {
        super();
        readByte(buffer);
    }

    private void readByte(byte[] buffer){
        ByteArrayInputStream stream = new ByteArrayInputStream(buffer);
        DataInputStream in = new DataInputStream(stream);
        try {
            int size = in.readUnsignedByte();
            byte[] src = new byte[size];
            in.read(src);
            setRoutename(new String(src,"GBK"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getRoutename() {
        return linename;
    }

    public void setRoutename(String content) {
        linename = content;
    }

    @Override
    public String toString() {
        return "NatifyRoutenameMsg [linename=" + linename+ "]";
    }

}
