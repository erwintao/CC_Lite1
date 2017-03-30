package DataPackage;

/**
 * Created by xiaodongtao on 17/3/29.
 */

public interface IPackage {
    public byte GetAddress();
    public byte GetType();

    public byte[] GetKeyWords();
    public boolean GetCurrentState(int index);
    public boolean GetControlState(int index);
    public byte GetLightness(int index);
    public void SetControlState(int index, boolean state);
    public void SetLightness(int index, byte lightness);

    public byte[] toArray();
}
