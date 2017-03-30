package DataPackage;

/**
 * Created by xiaodongtao on 17/3/29.
 * for module type is 2-SW8
 */

public class PackageElectricRelay implements IPackage {
    protected static final byte[] keyWords = new byte[]{(byte)0xAB, (byte)0xBB};
    protected byte[] data;

    @Override
    public byte GetAddress() {
        return data[5];
    }

    @Override
    public byte GetType() {
        return data[3];
    }

    @Override
    public byte[] GetKeyWords() {
        return keyWords;
    }

    public PackageElectricRelay(byte[] data){
        this.data = data;
    }

    public static PackageElectricRelay isKindof(byte[] data){
        if (data == null || data.length < 3){
            return null;
        }
        else{
            // compare with key words.
            if (keyWords[0] == data[0]
                    && keyWords[1] == data[1]
                    && data[3] == 2){
                return new PackageElectricRelay(data);
            }
            else{
                return null;
            }
        }
    }

    @Override
    public boolean GetCurrentState(int index){
        return ((data[8 + index / 4] >> (index % 4)) & 0x01) == 0x01;
    }

    @Override
    public boolean GetControlState(int index){
        return ((data[6 + index / 4] >> (index % 4)) & 0x01) == 0x01;
    }

    @Override
    public byte GetLightness(int index) {
        return (byte) (GetCurrentState(index) ? 100 : 0);
    }

    @Override
    public void SetControlState(int index, boolean state) {
        if (state) {
            data[6 + index / 4] |= (0x01 << index % 4);
        } else {
            data[6 + index / 4] &= ~(0x01 << index % 4);
        }
    }

    @Override
    public void SetLightness(int index, byte lightness) {
        return;
    }

    @Override
    public byte[] toArray() {
        return this.data;
    }
}
