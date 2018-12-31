package com.divoom.Divoom.enums;

import java.io.Serializable;

public enum GalleryEnum
  implements Serializable
{
  static
  {
    DESIGN_GALLERY = new GalleryEnum("DESIGN_GALLERY", 1);
    SAND_GALLERY = new GalleryEnum("SAND_GALLERY", 2);
    MULTI_SAND_GALLERY = new GalleryEnum("MULTI_SAND_GALLERY", 3);
    ANIMATION_GALLERY = new GalleryEnum("ANIMATION_GALLERY", 4);
    SCROLL_GALLERY = new GalleryEnum("SCROLL_GALLERY", 5);
    ANIMATION_DESIGN_GALLERY = new GalleryEnum("ANIMATION_DESIGN_GALLERY", 6);
    CHAT_DESIGN_GALLERY = new GalleryEnum("CHAT_DESIGN_GALLERY", 7);
    HOME_HEAD_GALLERY = new GalleryEnum("HOME_HEAD_GALLERY", 8);
    LED_MATRIX_GALLERY = new GalleryEnum("LED_MATRIX_GALLERY", 9);
    LIGHT_GALLERY = new GalleryEnum("LIGHT_GALLERY", 10);
    TIME_GALLEY = new GalleryEnum("TIME_GALLEY", 11);
    CHAT_GALLERY = new GalleryEnum("CHAT_GALLERY", 12);
    LIGHT_MAKE_GALLERY = new GalleryEnum("LIGHT_MAKE_GALLERY", 13);
    LIGHT_MAKE_PIC_FRAME_GALLERY = new GalleryEnum("LIGHT_MAKE_PIC_FRAME_GALLERY", 14);
    OTHER_NORMAL_GALLERY = new GalleryEnum("OTHER_NORMAL_GALLERY", 15);
    EDIT_DESIGN = new GalleryEnum("EDIT_DESIGN", 16);
    EDIT_SAND = new GalleryEnum("EDIT_SAND", 17);
    EDIT_ANI = new GalleryEnum("EDIT_ANI", 18);
    EDIT_SCROLL = new GalleryEnum("EDIT_SCROLL", 19);
  }
  
  private GalleryEnum() {}
}
