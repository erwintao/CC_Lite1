package DataPackage;

/**
 * Created by xiaodongtao on 17/3/29.
 * For module type == 3-dim4, 4-dima4, 5-dimr2
 */

public class PackageDimmer implements IPackage {
    protected static final byte[] keyWords = new byte[]{(byte)0xAB, (byte)0xBB};
    protected byte[] data;

    public PackageDimmer(byte[] data){
        this.data = data;
    }

    public static PackageDimmer isKindof(byte[] data){
        if (data == null || data.length < 3){
            return null;
        }
        else{
            // compare with key words.
            if (keyWords[0] == data[0]
                    && keyWords[1] == data[1]
                    && data[3] != 2){
                return new PackageDimmer(data);
            }
            else{
                return null;
            }
        }
    }

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

    @Override
    public boolean GetCurrentState(int index){
        return ((data[8 + index / 4] >> (index % 4)) & 0x01) == 0x01;
    }
    @Override
    public boolean GetControlState(int index){
        return ((data[6 + index / 4] >> (index % 4)) & 0x01) == 0x01;
    }
    @Override
    public byte GetLightness(int index){
        return data[10 + index * 4];
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
        data[10 + index * 4] = lightness;
    }

    @Override
    public byte[] toArray() {
        return this.data;
    }
}
