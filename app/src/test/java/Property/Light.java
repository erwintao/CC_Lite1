package Property;

/**
 * Created by xiaodongtao on 17/3/29.
 */

public class Light {
    int loopIndex;
    byte moduleType;
    byte moduleAddress;

    public Light(int loopIndex, byte type, byte address) {
        this.loopIndex = loopIndex;
        this.moduleType = type;
        this.moduleAddress = address;
    }

    public void SetIndex(int loopIndex) {
        this.loopIndex = loopIndex;
    }

    public void SetType(byte type) {
        this.moduleType = type;
    }

    public void SetAddress(byte address) {
        this.moduleAddress = address;
    }
}
