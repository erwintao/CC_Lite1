package DataPackage;

import java.io.NotActiveException;

/**
 * Created by xiaodongtao on 17/3/29.
 * module type is 0
 */

public class PackageRoomStatus implements IPackage {
    protected static final byte[] keyWords = new byte[]{(byte)0xA3, (byte)0xB3};
    protected byte[] data;

    public PackageRoomStatus(byte[] data)
    {
        this.data = data;
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
    public boolean GetCurrentState(int index) {
        if (RoomStatusIndex.DND.ordinal() == index){
            return (data[9] & 0x01) > 0;
        }
        else if (RoomStatusIndex.MUR.ordinal() == index){
            return (data[9] & 0x02) > 0;
        }
        else {
            return false;
        }
    }

    @Override
    public boolean GetControlState(int index) {
        return false;
    }

    @Override
    public byte GetLightness(int index) {
        return 0;
    }

    @Override
    public void SetControlState(int index, boolean state) {
        return;
    }

    public byte[] SetState(int index, boolean state) throws NotActiveException {
        // copy all current state and set the target state with parameter.
        // return a new data package with keywords{0xA8 0xB8}.
        throw new NotActiveException();
    }

    @Override
    public void SetLightness(int index, byte lightness) {
        return;
    }

    @Override
    public byte[] toArray() {
        return new byte[0];
    }

    public static PackageRoomStatus isKindof(byte[] data){
        if (data == null || data.length < 3){
            return null;
        }
        else{
            // compare with key words.
            if (keyWords[0] == data[0]
                    && keyWords[1] == data[1]
                    && data[3] == 0){
                return new PackageRoomStatus(data);
            }
            else{
                return null;
            }
        }
    }
}
