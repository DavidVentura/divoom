package com.divoom.Divoom.enums;

public enum GalleryFileTypeEnum
{
  private int value;
  
  static
  {
    ANIMATION = new GalleryFileTypeEnum("ANIMATION", 1, 1);
    MULTI_PICTURE = new GalleryFileTypeEnum("MULTI_PICTURE", 2, 2);
    MULTI_ANIMATION = new GalleryFileTypeEnum("MULTI_ANIMATION", 3, 3);
    LED = new GalleryFileTypeEnum("LED", 4, 4);
    ALL = new GalleryFileTypeEnum("ALL", 5, 5);
    SAND = new GalleryFileTypeEnum("SAND", 6, 6);
    DESIGN_HEAD_DEVICE = new GalleryFileTypeEnum("DESIGN_HEAD_DEVICE", 7, 101);
    DESIGN_SAND = new GalleryFileTypeEnum("DESIGN_SAND", 8, 102);
    DESIGN_IMPORT = new GalleryFileTypeEnum("DESIGN_IMPORT", 9, 103);
  }
  
  private GalleryFileTypeEnum(int paramInt)
  {
    this.value = paramInt;
  }
  
  public int getValue()
  {
    return this.value;
  }
  
  public void setValue(int paramInt)
  {
    this.value = paramInt;
  }
}
