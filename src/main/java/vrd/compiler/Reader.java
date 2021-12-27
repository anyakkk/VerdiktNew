package vrd.compiler;

public class Reader {
    String str;
    int pos;
    int fillPos;
    int inLine;

    public Reader(String str) {
        this.str = str;
        this.pos = 0;
        this.fillPos = pos;
        this.inLine = 0;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public char nowCh() {
        return str.charAt(pos);
    }

    public void nextCh() {
        if (isNotOverflow() && (nowCh() == '\n')) {
            inLine = 0;
        }
        else inLine++;
        pos = pos + 1;
    }

    public int getPosInLine() {
        return inLine;
    }

    public String readLine() {
        this.rem();
        while (this.isNotOverflow() && (this.nowCh() != '\n')) {
            this.nextCh();
        }
        return this.interval();
    }
    public String readLineNoEnd() {
        String line = readLine();
        int i = line.length() - 1;
        while ((i >= 0) && ((line.charAt(i) == ' ') || (line.charAt(i) == '\r')))
            --i;
        return line.substring(0, i+1);
    }
    public String interval() {
        String substr = str.substring(fillPos, pos);
        fillPos = pos;
        return substr;
    }

    public void rem() {
        fillPos = pos;
    }

    public boolean isNotOverflow() {
        return this.pos < this.str.length();
    }

}
