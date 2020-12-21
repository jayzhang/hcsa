package io.github.jayzhang.hcsa;


public class Highlight {

    public int offset; // required

    public int length; // required

    public int field; // required
    
    
    public Highlight(int o, int l, int f)
    {
        this.offset = o;
        this.length = l;
        this.field = f;
    }

    public int getOffset()
    {
        return offset;
    }

    public void setOffset(int offset)
    {
        this.offset = offset;
    }

    public int getLength()
    {
        return length;
    }

    public void setLength(int length)
    {
        this.length = length;
    }

    public int getField()
    {
        return field;
    }

    public void setField(int field)
    {
        this.field = field;
    }

}

