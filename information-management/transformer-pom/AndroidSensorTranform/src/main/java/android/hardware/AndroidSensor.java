/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package android.hardware;

/**
 *
 * @author hungld
 */
public class AndroidSensor {

    int mFifoMaxEventCount;
    int mFifoReservedEventCount;
    int mFlags;
    int mHandle;
    int mMaxDelay;
    float mMaxRange;
    int mMinDelay;
    String mName;
    float mPower;
    String mRequiredPermission;
    float mResolution;
    String mStringType;
    int mType;
    String mVendor;
    int mVersion;

    public AndroidSensor() {
    }

    public int getmFifoMaxEventCount() {
        return mFifoMaxEventCount;
    }

    public void setmFifoMaxEventCount(int mFifoMaxEventCount) {
        this.mFifoMaxEventCount = mFifoMaxEventCount;
    }

    public int getmFifoReservedEventCount() {
        return mFifoReservedEventCount;
    }

    public void setmFifoReservedEventCount(int mFifoReservedEventCount) {
        this.mFifoReservedEventCount = mFifoReservedEventCount;
    }

    public int getmFlags() {
        return mFlags;
    }

    public void setmFlags(int mFlags) {
        this.mFlags = mFlags;
    }

    public int getmHandle() {
        return mHandle;
    }

    public void setmHandle(int mHandle) {
        this.mHandle = mHandle;
    }

    public int getmMaxDelay() {
        return mMaxDelay;
    }

    public void setmMaxDelay(int mMaxDelay) {
        this.mMaxDelay = mMaxDelay;
    }

    public float getmMaxRange() {
        return mMaxRange;
    }

    public void setmMaxRange(float mMaxRange) {
        this.mMaxRange = mMaxRange;
    }

    public int getmMinDelay() {
        return mMinDelay;
    }

    public void setmMinDelay(int mMinDelay) {
        this.mMinDelay = mMinDelay;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public float getmPower() {
        return mPower;
    }

    public void setmPower(float mPower) {
        this.mPower = mPower;
    }

    public String getmRequiredPermission() {
        return mRequiredPermission;
    }

    public void setmRequiredPermission(String mRequiredPermission) {
        this.mRequiredPermission = mRequiredPermission;
    }

    public float getmResolution() {
        return mResolution;
    }

    public void setmResolution(float mResolution) {
        this.mResolution = mResolution;
    }

    public String getmStringType() {
        return mStringType;
    }

    public void setmStringType(String mStringType) {
        this.mStringType = mStringType;
    }

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

    public String getmVendor() {
        return mVendor;
    }

    public void setmVendor(String mVendor) {
        this.mVendor = mVendor;
    }

    public int getmVersion() {
        return mVersion;
    }

    public void setmVersion(int mVersion) {
        this.mVersion = mVersion;
    }

}
