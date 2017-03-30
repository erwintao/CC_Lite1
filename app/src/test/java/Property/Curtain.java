package Property;

/**
 * Created by xiaodongtao on 17/3/29.
 */

public class Curtain {
    int openIndex;
    int closeIndex;
    byte moduleType;
    byte moduleAddress;

    public Curtain(int indexopen, int indexclose, byte type, byte address) {
        this.openIndex = indexopen;
        this.closeIndex = indexclose;
        this.moduleType = type;
        this.moduleAddress = address;
    }

    public void SetOpenIndex(int indexopen) {
        this.openIndex = indexopen;
    }

    public void SetCloseIndex(int indexclose) {
        this.closeIndex = indexclose;
    }

    public void SetType(byte type) {
        this.moduleType = type;
    }

    public void SetAddress(byte address) {
        this.moduleAddress = address;
    }
}
