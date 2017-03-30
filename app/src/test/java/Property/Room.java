package Property;

import java.io.IOException;

import Communication.CommViaTCP;
import DataPackage.IPackage;
import DataPackage.PackageDimmer;
import DataPackage.PackageElectricRelay;
import DataPackage.PackageRoomStatus;

/**
 * Created by xiaodongtao on 17/3/29.
 */

public class Room implements INewDataEvent{
    CommViaTCP comm;
    /**
     * 需要自动识别是哪个数据包对应哪个模块
     */
    IPackage[] modules = new IPackage[4];
    //PackageDimmer dimmer[] = new PackageDimmer[3];
    //PackageElectricRelay sw8;

    boolean rs_DND;
    boolean rs_MUR;

    Light ceilingLight;
    Light wallLamp;
    Light tableLamp;
    Curtain curtain;
    String IPAddress;
    int Port;

    public Room() {
        comm = new CommViaTCP(this);
    }

    public void UI_NetConfig(String ipaddress, int port) {
        this.IPAddress = ipaddress;
        this.Port = port;
    }

    public int UI_Connect2() {
        return this.comm.Connect2(this.IPAddress, this.Port);
    }

    public int UI_Disconnect() {
        return this.comm.Disconnect();
    }

    public boolean IsConnected() {
        return this.comm.IsConnected();
    }

    /**
     * @param type
     * @param address
     * @return
     */
    public IPackage findModule(byte type, byte address) {
        for (IPackage ip : this.modules) {
            if (ip.getClass().equals(PackageDimmer.class)) {
                PackageDimmer dim = (PackageDimmer) ip;
                if (dim.GetType() == type && dim.GetAddress() == address) {
                    return dim;
                }
            } else if (ip.getClass().equals(PackageElectricRelay.class)) {
                PackageElectricRelay er = (PackageElectricRelay) ip;
                if (er.GetType() == type && er.GetAddress() == address) {
                    return er;
                }
            }
        }
        return null;
    }

    /**
     * @param loopIndex
     * @param type
     * @param address
     */
    public void UI_SetCeilingLight(int loopIndex, byte type, byte address) {
        if (this.ceilingLight == null) {
            this.ceilingLight = new Light(loopIndex, type, address);
        } else {
            this.ceilingLight.SetIndex(loopIndex);
            this.ceilingLight.SetType(type);
            this.ceilingLight.SetAddress(address);
        }
    }

    /**
     * Switch on/off ceiling light.
     *
     * @param on
     * @return
     */
    public int UI_SwitchCeilingLight(boolean on) {
        IPackage dim = this.findModule(
                this.ceilingLight.moduleType,
                this.ceilingLight.moduleAddress);
        if (dim == null) {
            return -1;
        } else {
            dim.SetControlState(this.ceilingLight.loopIndex, on);
            try {
                comm.Send(dim.toArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    /**
     * @param loopIndex
     * @param type
     * @param address
     */
    public void UI_SetWallLamp(int loopIndex, byte type, byte address) {
        if (this.wallLamp == null) {
            this.wallLamp = new Light(loopIndex, type, address);
        } else {
            this.wallLamp.SetIndex(loopIndex);
            this.wallLamp.SetType(type);
            this.wallLamp.SetAddress(address);
        }
    }

    /**
     * Switch on/off wall lamp.
     *
     * @param on
     * @return
     */
    public int UI_SwitchWallLamp(boolean on) {
        IPackage tmp = this.findModule(
                this.wallLamp.moduleType,
                this.wallLamp.moduleAddress);
        if (tmp == null) {
            return -1;
        } else {
            tmp.SetControlState(this.wallLamp.loopIndex, on);
            try {
                comm.Send(tmp.toArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    /**
     * @param loopIndex
     * @param type
     * @param address
     */
    public void UI_SetTableLamp(int loopIndex, byte type, byte address) {
        if (this.tableLamp == null) {
            this.tableLamp = new Light(loopIndex, type, address);
        } else {
            this.tableLamp.SetIndex(loopIndex);
            this.tableLamp.SetType(type);
            this.tableLamp.SetAddress(address);
        }
    }

    /**
     * Switch table lamp on/off and dim it's lightness.
     *
     * @param on
     * @param lightness
     * @return
     */
    public int UI_DimTableLamp(boolean on, byte lightness) {
        PackageDimmer dim = (PackageDimmer) this.findModule(
                this.tableLamp.moduleType,
                this.tableLamp.moduleAddress);
        if (dim == null) {
            return -1;
        } else {
            dim.SetControlState(this.tableLamp.loopIndex, on);
            dim.SetLightness(this.tableLamp.loopIndex, lightness);
            try {
                comm.Send(dim.toArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    /**
     * @param indexopen     对应打开窗帘的回路序号
     * @param indexclose    对应关闭窗帘的回路序号
     * @param type          对应硬件类型
     * @param address       对应硬件拨码地址
     */
    public void UI_SetCurtain(int indexopen, int indexclose, byte type, byte address) {
        if (this.curtain == null) {
            this.curtain = new Curtain(indexopen, indexclose, type, address);
        } else {
            this.curtain.SetOpenIndex(indexopen);
            this.curtain.SetCloseIndex(indexclose);
            this.curtain.SetType(type);
            this.curtain.SetAddress(address);
        }
    }

    /**
     * 必须同时设置两个参数!!!
     *
     * @param on     switch motor of curtain on/off.
     * @param isOpen switch motor's direction to open/close.
     * @return -1 not found target.
     */
    public int UI_SwitchCurtain(boolean on, boolean isOpen) {
        PackageElectricRelay sw8 = (PackageElectricRelay) this.findModule(
                this.curtain.moduleType,
                this.curtain.moduleAddress);
        if (sw8 == null) {
            return -1;
        } else {
            if (on) {
                // if start opening or closing then...
                if (isOpen) {
                    // if start opening then turn down "close loop" and turn up "open loop".
                    sw8.SetControlState(this.curtain.openIndex, true);
                    sw8.SetControlState(this.curtain.closeIndex, false);
                } else {
                    sw8.SetControlState(this.curtain.openIndex, false);
                    sw8.SetControlState(this.curtain.closeIndex, true);
                }
            } else {
                // if stop opening or closing then turn down both loops
                sw8.SetControlState(this.curtain.openIndex, false);
                sw8.SetControlState(this.curtain.closeIndex, false);
            }
            try {
                comm.Send(sw8.toArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    /**
     *
     * @param pkg
     */
    @Override
    public void newDataPackage(IPackage pkg) {
        //???!!!
    }
}
