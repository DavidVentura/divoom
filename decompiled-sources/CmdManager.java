package com.divoom.Divoom.bluetooth;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.TextUtils;
import com.divoom.Divoom.GlobalApplication;
import com.divoom.Divoom.GlobalApplication.BlueModeEnum;
import com.divoom.Divoom.b.g.u;
import com.divoom.Divoom.bean.NetTemp;
import com.divoom.Divoom.ndk.NDKMain;
import com.divoom.Divoom.utils.aw;
import com.divoom.Divoom.utils.ax;
import com.divoom.Divoom.utils.l;
import com.divoom.Divoom.utils.w;
import io.reactivex.b.e;
import io.reactivex.h;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import org.xutils.common.util.LogUtil;

public class CmdManager
{
  private static int a = 16384;
  
  public static byte[] A()
  {
    return m.a(SppProc.CMD_TYPE.SPP_GET_DEVICE_TEMP_INFO, null);
  }
  
  public static byte[] B()
  {
    return m.a(SppProc.CMD_TYPE.SPP_GET_NET_TEMP_DISP_INFO, null);
  }
  
  public static byte[] C()
  {
    com.divoom.Divoom.utils.d.a("----------->发送获取屏幕亮度");
    return m.a(SppProc.CMD_TYPE.SPP_LIGHT_CURRENT_LEVEL, null);
  }
  
  public static byte[] D()
  {
    return m.a(SppProc.CMD_TYPE.SPP_SAND_PAINT_CTRL, new byte[] { 1 });
  }
  
  public static byte[] E()
  {
    return m.a(SppProc.CMD_TYPE.SPP_DRAWING_MUL_ENCODE_GIF_PLAY, null);
  }
  
  public static byte[] F()
  {
    org.greenrobot.eventbus.c.a().d(new com.divoom.Divoom.b.h.c());
    int i = (byte)MultiControlEnum.getAllInfo._value;
    return m.a(SppProc.CMD_TYPE.SPP_SET_MUL_DEVICE_CTRL, new byte[] { i });
  }
  
  public static byte[] G()
  {
    int i = (byte)MultiControlEnum.exitMulti._value;
    return m.a(SppProc.CMD_TYPE.SPP_SET_MUL_DEVICE_CTRL, new byte[] { i });
  }
  
  public static byte[] H()
  {
    int i = (byte)LightEnum.HOT_MODE._value;
    return m.a(SppProc.CMD_TYPE.SPP_SET_BOX_MODE, new byte[] { i });
  }
  
  public static byte[] I()
  {
    return m.a(SppProc.CMD_TYPE.SPP_RESET_NOTIFICATIONS, null);
  }
  
  public static byte[] J()
  {
    return m.a(SppProc.CMD_TYPE.SPP_GET_SD_MUSIC_LIST_TOTAL_NUM, null);
  }
  
  public static byte[] K()
  {
    return m.a(SppProc.CMD_TYPE.SPP_APP_NEED_GET_MUSIC_LIST, null);
  }
  
  public static byte[] L()
  {
    l.c("octopus.CmdManager", "开始获取SD卡状态");
    return m.a(SppProc.CMD_TYPE.SPP_SEND_SD_TF_STATUS, null);
  }
  
  public static byte[] M()
  {
    return m.a(SppProc.CMD_TYPE.SPP_GET_SD_MUSIC_INFO, null);
  }
  
  public static byte[] N()
  {
    return m.a(SppProc.CMD_TYPE.SPP_MODIFY_RHYTHM_ITEMS, new byte[] { -1 });
  }
  
  public static byte[] O()
  {
    byte[] arrayOfByte = new byte[1];
    arrayOfByte[0] = -1;
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("getSleepCtrlMode----------》");
    localStringBuilder.append(arrayOfByte[0]);
    LogUtil.e(localStringBuilder.toString());
    return m.a(SppProc.CMD_TYPE.SPP_SET_SLEEP_CTRL_MODE, arrayOfByte);
  }
  
  public static int a(List<byte[]> paramList)
  {
    int i = 0;
    int j = 0;
    while (i < paramList.size())
    {
      j += GlobalApplication.b().g().PixelEncodeSixteen((byte[])paramList.get(i), 1, 1000).length;
      i += 1;
    }
    paramList = new StringBuilder();
    paramList.append("len ");
    paramList.append(j);
    l.c("octopus.CmdManager", paramList.toString());
    return j;
  }
  
  public static List<byte[]> a(byte paramByte1, byte paramByte2, int paramInt, List<byte[]> paramList)
  {
    ArrayList localArrayList = new ArrayList();
    Object localObject;
    if (GlobalApplication.BlueModeEnum.getMode() == GlobalApplication.BlueModeEnum.OldMode)
    {
      b1 = 0;
      while (b1 < paramByte2)
      {
        arrayOfByte = new byte['Ǵ'];
        arrayOfByte[0] = paramByte1;
        arrayOfByte[1] = paramByte2;
        arrayOfByte[2] = ((byte)(b1 & 0xFF));
        arrayOfByte[3] = ((byte)(paramInt / 100 & 0xFF));
        b2 = 0;
        i = 4;
        while (b2 < 363)
        {
          if (b2 == 362) {
            arrayOfByte[i] = ((byte)(j(((byte[])paramList.get(b1))[b2]) >> 4));
          } else {
            arrayOfByte[i] = ((byte)(j(((byte[])paramList.get(b1))[b2]) >> 4 | j(((byte[])paramList.get(b1))[(b2 + 1)]) >> 4 << 4));
          }
          i += 1;
          b2 += 2;
        }
        localObject = new byte[i];
        System.arraycopy(arrayOfByte, 0, localObject, 0, localObject.length);
        localArrayList.add(m.a(SppProc.CMD_TYPE.SPP_SET_ALARM_TIME_GIF, (byte[])localObject));
        b1 += 1;
      }
      return localArrayList;
    }
    byte[] arrayOfByte = new byte[((byte[])paramList.get(0)).length * paramByte2];
    byte b1 = 0;
    byte b2 = 0;
    while (b1 < paramList.size())
    {
      System.arraycopy(paramList.get(b1), 0, arrayOfByte, b2, ((byte[])paramList.get(b1)).length);
      b2 += ((byte[])paramList.get(b1)).length;
      b1 += 1;
    }
    paramList = GlobalApplication.b().g().PixelEncodeSixteen(arrayOfByte, paramByte2, paramInt);
    int i = paramList.length;
    int j = (int)Math.ceil(i / 200.0F);
    paramInt = 0;
    b1 = 0;
    while (paramInt < j)
    {
      if (paramInt != j - 1) {
        b2 = 200;
      } else {
        b2 = i - paramInt * 200;
      }
      arrayOfByte = new byte[b2 + 4];
      arrayOfByte[0] = ((byte)(paramByte1 & 0xFF));
      arrayOfByte[1] = ((byte)(i & 0xFF));
      arrayOfByte[2] = ((byte)(i >> 8 & 0xFF));
      arrayOfByte[3] = ((byte)paramInt);
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("ret ");
      ((StringBuilder)localObject).append(paramList.length);
      ((StringBuilder)localObject).append(" ret_offset ");
      ((StringBuilder)localObject).append(b1);
      ((StringBuilder)localObject).append(" packet ");
      ((StringBuilder)localObject).append(arrayOfByte.length);
      l.c("octopus.CmdManager", ((StringBuilder)localObject).toString());
      System.arraycopy(paramList, b1, arrayOfByte, 4, b2);
      b1 += b2;
      localArrayList.add(m.a(SppProc.CMD_TYPE.SPP_SET_ALARM_TIME_GIF, arrayOfByte));
      paramInt += 1;
    }
    return localArrayList;
  }
  
  public static List<byte[]> a(byte paramByte, int paramInt, List<byte[]> paramList)
  {
    ArrayList localArrayList = new ArrayList();
    Object localObject;
    if (GlobalApplication.BlueModeEnum.getMode() == GlobalApplication.BlueModeEnum.OldMode)
    {
      b1 = 0;
      while (b1 < paramByte)
      {
        arrayOfByte = new byte['Ǵ'];
        arrayOfByte[0] = paramByte;
        arrayOfByte[1] = ((byte)(b1 & 0xFF));
        arrayOfByte[2] = ((byte)(paramInt / 100 & 0xFF));
        i = 0;
        b2 = 3;
        while (i < 363)
        {
          if (i == 362) {
            arrayOfByte[b2] = ((byte)(j(((byte[])paramList.get(b1))[i]) >> 4));
          } else {
            arrayOfByte[b2] = ((byte)(j(((byte[])paramList.get(b1))[i]) >> 4 | j(((byte[])paramList.get(b1))[(i + 1)]) >> 4 << 4));
          }
          b2 += 1;
          i += 2;
        }
        localObject = new byte[b2];
        i = 0;
        while (i < b2)
        {
          localObject[i] = arrayOfByte[i];
          i += 1;
        }
        localArrayList.add(m.a(SppProc.CMD_TYPE.SPP_SET_USER_GIF, (byte[])localObject));
        b1 += 1;
      }
      return localArrayList;
    }
    byte[] arrayOfByte = new byte[((byte[])paramList.get(0)).length * paramByte];
    byte b1 = 0;
    byte b2 = 0;
    while (b1 < paramList.size())
    {
      System.arraycopy(paramList.get(b1), 0, arrayOfByte, b2, ((byte[])paramList.get(b1)).length);
      b2 += ((byte[])paramList.get(b1)).length;
      b1 += 1;
    }
    paramList = GlobalApplication.b().g().PixelEncodeSixteen(arrayOfByte, paramByte, paramInt);
    int i = paramList.length;
    double d = i;
    Double.isNaN(d);
    int j = (int)Math.ceil(d / 200.0D);
    paramInt = 0;
    b1 = 0;
    while (paramInt < j)
    {
      if (paramInt != j - 1) {
        b2 = 200;
      } else {
        b2 = i - paramInt * 200;
      }
      arrayOfByte = new byte[b2 + 3];
      arrayOfByte[0] = 1;
      arrayOfByte[1] = ((byte)(b2 & 0xFF));
      arrayOfByte[2] = ((byte)(b2 >> 8 & 0xFF));
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("ret ");
      ((StringBuilder)localObject).append(paramList.length);
      ((StringBuilder)localObject).append(" ret_offset ");
      ((StringBuilder)localObject).append(b1);
      ((StringBuilder)localObject).append(" packet ");
      ((StringBuilder)localObject).append(arrayOfByte.length);
      l.c("octopus.CmdManager", ((StringBuilder)localObject).toString());
      System.arraycopy(paramList, b1, arrayOfByte, 3, b2);
      b1 += b2;
      localArrayList.add(m.a(SppProc.CMD_TYPE.SPP_SET_USER_GIF, arrayOfByte));
      paramInt += 1;
    }
    return localArrayList;
  }
  
  public static List<byte[]> a(int paramInt, List<byte[]> paramList)
  {
    ArrayList localArrayList = new ArrayList();
    Object localObject1 = new ArrayList();
    int i = 0;
    while (i < paramList.size())
    {
      ((List)localObject1).add(GlobalApplication.b().g().PixelEncodeSixteen((byte[])paramList.get(i), 1, paramInt));
      i += 1;
    }
    paramList = ((List)localObject1).iterator();
    paramInt = 0;
    while (paramList.hasNext()) {
      paramInt += ((byte[])paramList.next()).length;
    }
    paramList = new byte[paramInt];
    localObject1 = ((List)localObject1).iterator();
    i = 0;
    Object localObject2;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (byte[])((Iterator)localObject1).next();
      System.arraycopy(localObject2, 0, paramList, i, localObject2.length);
      i += localObject2.length;
    }
    double d = paramInt;
    Double.isNaN(d);
    int m = (int)Math.ceil(d / 200.0D);
    i = 0;
    int j = 0;
    while (i < m)
    {
      int k;
      if (i != m - 1) {
        k = 200;
      } else {
        k = paramInt - i * 200;
      }
      localObject1 = new byte[k + 3];
      localObject1[0] = 1;
      localObject1[1] = ((byte)(k & 0xFF));
      localObject1[2] = ((byte)(k >> 8 & 0xFF));
      localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append("ret ");
      ((StringBuilder)localObject2).append(paramList.length);
      ((StringBuilder)localObject2).append(" ret_offset ");
      ((StringBuilder)localObject2).append(j);
      ((StringBuilder)localObject2).append(" packet ");
      ((StringBuilder)localObject2).append(localObject1.length);
      l.c("octopus.CmdManager", ((StringBuilder)localObject2).toString());
      System.arraycopy(paramList, j, localObject1, 3, k);
      j += k;
      localArrayList.add(m.a(SppProc.CMD_TYPE.SPP_SET_USER_GIF, (byte[])localObject1));
      i += 1;
    }
    return localArrayList;
  }
  
  public static List<byte[]> a(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
  {
    org.greenrobot.eventbus.c.a().d(new com.divoom.Divoom.b.h.c());
    paramArrayOfByte = GlobalApplication.b().g().PixelEncodeSixteen(paramArrayOfByte, paramInt2, paramInt3);
    int j = paramArrayOfByte.length;
    int k = (int)Math.ceil(j / 200.0F);
    ArrayList localArrayList = new ArrayList();
    paramInt2 = 0;
    paramInt3 = 0;
    while (paramInt2 < k)
    {
      int i;
      if (paramInt2 != k - 1) {
        i = 200;
      } else {
        i = j - paramInt2 * 200;
      }
      byte[] arrayOfByte = new byte[i + 4];
      arrayOfByte[0] = ((byte)paramInt1);
      arrayOfByte[1] = ((byte)(j & 0xFF));
      arrayOfByte[2] = ((byte)(j >> 8 & 0xFF));
      arrayOfByte[3] = ((byte)paramInt2);
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("ret ");
      localStringBuilder.append(paramArrayOfByte.length);
      localStringBuilder.append(" ret_offset ");
      localStringBuilder.append(paramInt3);
      localStringBuilder.append(" packet ");
      localStringBuilder.append(arrayOfByte.length);
      l.c("octopus.CmdManager", localStringBuilder.toString());
      System.arraycopy(paramArrayOfByte, paramInt3, arrayOfByte, 4, i);
      paramInt3 += i;
      localArrayList.add(m.a(SppProc.CMD_TYPE.SPP_DRAWING_ENCODE_PLAY, arrayOfByte));
      paramInt2 += 1;
    }
    return localArrayList;
  }
  
  public static List<byte[]> a(List<byte[]> paramList, int paramInt1, int paramInt2)
  {
    org.greenrobot.eventbus.c.a().d(new com.divoom.Divoom.b.h.c());
    ArrayList localArrayList = new ArrayList();
    Object localObject1 = new ArrayList();
    paramInt1 = 0;
    while (paramInt1 < paramList.size())
    {
      ((List)localObject1).add(GlobalApplication.b().g().PixelEncodeSixteen((byte[])paramList.get(paramInt1), 1, paramInt2));
      paramInt1 += 1;
    }
    paramList = ((List)localObject1).iterator();
    paramInt1 = 0;
    while (paramList.hasNext()) {
      paramInt1 += ((byte[])paramList.next()).length;
    }
    paramList = new byte[paramInt1];
    localObject1 = ((List)localObject1).iterator();
    paramInt2 = 0;
    Object localObject2;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (byte[])((Iterator)localObject1).next();
      System.arraycopy(localObject2, 0, paramList, paramInt2, localObject2.length);
      paramInt2 += localObject2.length;
    }
    int k = (int)Math.ceil(paramInt1 / 200.0F);
    paramInt2 = 0;
    int i = 0;
    while (paramInt2 < k)
    {
      int j;
      if (paramInt2 != k - 1) {
        j = 200;
      } else {
        j = paramInt1 - paramInt2 * 200;
      }
      localObject1 = new byte[j + 4];
      localObject1[0] = 1;
      localObject1[1] = ((byte)(paramInt1 & 0xFF));
      localObject1[2] = ((byte)(paramInt1 >> 8 & 0xFF));
      localObject1[3] = ((byte)paramInt2);
      localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append("ret ");
      ((StringBuilder)localObject2).append(paramList.length);
      ((StringBuilder)localObject2).append(" ret_offset ");
      ((StringBuilder)localObject2).append(i);
      ((StringBuilder)localObject2).append(" packet ");
      ((StringBuilder)localObject2).append(localObject1.length);
      l.c("octopus.CmdManager", ((StringBuilder)localObject2).toString());
      System.arraycopy(paramList, i, localObject1, 4, j);
      i += j;
      localArrayList.add(m.a(SppProc.CMD_TYPE.SPP_SCROLL, (byte[])localObject1));
      paramInt2 += 1;
    }
    return localArrayList;
  }
  
  public static List<byte[]> a(boolean paramBoolean, byte paramByte, int paramInt, List<byte[]> paramList)
  {
    ArrayList localArrayList = new ArrayList();
    Object localObject;
    if (GlobalApplication.BlueModeEnum.getMode() == GlobalApplication.BlueModeEnum.OldMode)
    {
      b1 = 0;
      while (b1 < paramByte)
      {
        arrayOfByte = new byte['Ǵ'];
        arrayOfByte[0] = ((byte)paramBoolean);
        arrayOfByte[1] = paramByte;
        arrayOfByte[2] = ((byte)(b1 & 0xFF));
        arrayOfByte[3] = ((byte)(paramInt / 100 & 0xFF));
        i = 0;
        b2 = 4;
        while (i < 363)
        {
          if (i == 362) {
            arrayOfByte[b2] = ((byte)(j(((byte[])paramList.get(b1))[i]) >> 4));
          } else {
            arrayOfByte[b2] = ((byte)(j(((byte[])paramList.get(b1))[i]) >> 4 | j(((byte[])paramList.get(b1))[(i + 1)]) >> 4 << 4));
          }
          b2 += 1;
          i += 2;
        }
        localObject = new byte[b2];
        i = 0;
        while (i < b2)
        {
          localObject[i] = arrayOfByte[i];
          i += 1;
        }
        localArrayList.add(m.a(SppProc.CMD_TYPE.SPP_SET_BOOT_GIF, (byte[])localObject));
        b1 += 1;
      }
    }
    byte[] arrayOfByte = new byte[((byte[])paramList.get(0)).length * paramByte];
    byte b1 = 0;
    byte b2 = 0;
    while (b1 < paramList.size())
    {
      System.arraycopy(paramList.get(b1), 0, arrayOfByte, b2, ((byte[])paramList.get(b1)).length);
      b2 += ((byte[])paramList.get(b1)).length;
      b1 += 1;
    }
    paramList = GlobalApplication.b().g().PixelEncodeSixteen(arrayOfByte, paramByte, paramInt);
    int i = paramList.length;
    int j = (int)Math.ceil(i / 200.0F);
    paramInt = 0;
    b1 = 0;
    while (paramInt < j)
    {
      if (paramInt != j - 1) {
        b2 = 200;
      } else {
        b2 = i - paramInt * 200;
      }
      arrayOfByte = new byte[b2 + 4];
      arrayOfByte[0] = ((byte)paramBoolean);
      arrayOfByte[1] = ((byte)(i & 0xFF));
      arrayOfByte[2] = ((byte)(i >> 8 & 0xFF));
      arrayOfByte[3] = ((byte)paramInt);
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("ret ");
      ((StringBuilder)localObject).append(paramList.length);
      ((StringBuilder)localObject).append(" ret_offset ");
      ((StringBuilder)localObject).append(b1);
      ((StringBuilder)localObject).append(" packet ");
      ((StringBuilder)localObject).append(arrayOfByte.length);
      l.c("octopus.CmdManager", ((StringBuilder)localObject).toString());
      System.arraycopy(paramList, b1, arrayOfByte, 4, b2);
      b1 += b2;
      localArrayList.add(m.a(SppProc.CMD_TYPE.SPP_SET_BOOT_GIF, arrayOfByte));
      paramInt += 1;
    }
    return localArrayList;
  }
  
  public static List<byte[]> a(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    org.greenrobot.eventbus.c.a().d(new com.divoom.Divoom.b.h.c());
    paramArrayOfByte = GlobalApplication.b().g().PixelEncodeSixteen(paramArrayOfByte, paramInt1, paramInt2);
    int j = paramArrayOfByte.length;
    int k = (int)Math.ceil(j / 200.0F);
    ArrayList localArrayList = new ArrayList();
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("tol_len ");
    ((StringBuilder)localObject).append(j);
    l.c("octopus.CmdManager", ((StringBuilder)localObject).toString());
    if (j > a) {
      return localArrayList;
    }
    paramInt1 = 0;
    paramInt2 = 0;
    while (paramInt1 < k)
    {
      int i;
      if (paramInt1 != k - 1) {
        i = 200;
      } else {
        i = j - paramInt1 * 200;
      }
      localObject = new byte[i + 3];
      localObject[0] = ((byte)(j & 0xFF));
      localObject[1] = ((byte)(j >> 8 & 0xFF));
      localObject[2] = ((byte)paramInt1);
      System.arraycopy(paramArrayOfByte, paramInt2, localObject, 3, i);
      paramInt2 += i;
      localArrayList.add(m.a(SppProc.CMD_TYPE.SPP_SET_MUL_BOX_COLOR, (byte[])localObject));
      paramInt1 += 1;
    }
    return localArrayList;
  }
  
  public static void a(int paramInt1, int paramInt2)
  {
    int i = (byte)paramInt1;
    int j = (byte)(paramInt2 & 0xFF);
    int k = (byte)(paramInt2 >> 8 & 0xFF);
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("mode ");
    ((StringBuilder)localObject).append(paramInt1);
    ((StringBuilder)localObject).append(" speed ");
    ((StringBuilder)localObject).append(paramInt2);
    l.c("octopus.CmdManager", ((StringBuilder)localObject).toString());
    localObject = m.a(SppProc.CMD_TYPE.SPP_SCROLL, new byte[] { 0, i, j, k });
    k.a().a((byte[])localObject, true);
  }
  
  public static void a(int paramInt1, byte[] paramArrayOfByte, int paramInt2)
  {
    int m = paramArrayOfByte.length;
    int n = (int)Math.ceil(m / 200.0F);
    int i = 0;
    int j = 0;
    while (i < n)
    {
      int k;
      if (i != n - 1) {
        k = 200;
      } else {
        k = m - i * 200;
      }
      byte[] arrayOfByte = new byte[k + 4];
      arrayOfByte[0] = ((byte)(paramInt1 & 0xFF));
      arrayOfByte[1] = ((byte)(paramInt2 & 0xFF));
      arrayOfByte[2] = ((byte)(paramInt2 >> 8 & 0xFF));
      arrayOfByte[3] = ((byte)i);
      System.arraycopy(paramArrayOfByte, j, arrayOfByte, 4, k);
      j += k;
      arrayOfByte = m.a(SppProc.CMD_TYPE.SPP_SET_RHYTHM_GIF, arrayOfByte);
      k.a().a(arrayOfByte, true);
      i += 1;
    }
  }
  
  public static byte[] a()
  {
    org.greenrobot.eventbus.c.a().d(new com.divoom.Divoom.b.h.c());
    return m.a(SppProc.CMD_TYPE.SPP_SET_USER_GIF, new byte[] { 0, 0 });
  }
  
  public static byte[] a(byte paramByte)
  {
    return m.a(SppProc.CMD_TYPE.SPP_SET_CONNECTED_FLAG, new byte[] { paramByte });
  }
  
  public static byte[] a(byte paramByte1, byte paramByte2)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("闹钟------》start------>\t\t");
    localStringBuilder.append(paramByte1);
    localStringBuilder.append("pos----------->\t\t");
    localStringBuilder.append(paramByte2);
    LogUtil.e(localStringBuilder.toString());
    localStringBuilder = new StringBuilder();
    localStringBuilder.append("0:开始录音, 1:开始播放录音, 2:停止播放录音");
    localStringBuilder.append(paramByte1);
    com.divoom.Divoom.utils.d.a(localStringBuilder.toString());
    return m.a(SppProc.CMD_TYPE.SPP_SET_ALARM_VOICE_CTRL, new byte[] { paramByte1, paramByte2 });
  }
  
  public static byte[] a(byte paramByte1, byte paramByte2, byte paramByte3)
  {
    return m.a(SppProc.CMD_TYPE.SPP_SET_SLEEP_COLOR, new byte[] { paramByte1, paramByte2, paramByte3 });
  }
  
  public static byte[] a(byte paramByte1, byte paramByte2, byte paramByte3, byte paramByte4, byte paramByte5, byte paramByte6, byte paramByte7, String paramString)
  {
    byte[] arrayOfByte2 = new byte['Ǵ'];
    int k = 0;
    arrayOfByte2[0] = paramByte1;
    arrayOfByte2[1] = paramByte2;
    arrayOfByte2[2] = paramByte3;
    arrayOfByte2[3] = paramByte4;
    arrayOfByte2[4] = paramByte5;
    arrayOfByte2[5] = paramByte6;
    arrayOfByte2[6] = paramByte7;
    byte[] arrayOfByte1 = new byte[0];
    Object localObject = arrayOfByte1;
    if (paramString != null) {
      try
      {
        localObject = paramString.getBytes("UnicodeBigUnmarked");
      }
      catch (UnsupportedEncodingException paramString)
      {
        paramString.printStackTrace();
        localObject = arrayOfByte1;
      }
    }
    int j = 0;
    int i = 7;
    while (j < 32)
    {
      int m;
      if (j < localObject.length)
      {
        m = i + 1;
        arrayOfByte2[i] = localObject[j];
      }
      for (i = m;; i = m)
      {
        break;
        m = i + 1;
        arrayOfByte2[i] = 0;
      }
      j += 1;
    }
    paramString = new byte[i];
    j = k;
    while (j < i)
    {
      paramString[j] = arrayOfByte2[j];
      j += 1;
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("memorial send ");
    ((StringBuilder)localObject).append(aw.a(paramString));
    l.c("octopus.CmdManager", ((StringBuilder)localObject).toString());
    return m.a(SppProc.CMD_TYPE.SPP_SET_DIALY_TIME_EXT2, paramString);
  }
  
  public static byte[] a(byte paramByte1, byte paramByte2, byte paramByte3, byte paramByte4, byte paramByte5, int paramInt)
  {
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("------------>SPP_SEND_NET_TEMP_DISP_INFO=model=");
    ((StringBuilder)localObject).append(paramByte1);
    ((StringBuilder)localObject).append("  model2=");
    ((StringBuilder)localObject).append(paramByte2);
    ((StringBuilder)localObject).append("  model3=");
    ((StringBuilder)localObject).append(paramByte3);
    ((StringBuilder)localObject).append("  model4=");
    ((StringBuilder)localObject).append(paramByte4);
    ((StringBuilder)localObject).append(" model5= ");
    ((StringBuilder)localObject).append(paramByte5);
    ((StringBuilder)localObject).append("  time_second=");
    ((StringBuilder)localObject).append(paramInt);
    com.divoom.Divoom.utils.d.c(((StringBuilder)localObject).toString());
    localObject = new byte[7];
    localObject[0] = paramByte1;
    localObject[1] = paramByte2;
    localObject[2] = paramByte3;
    localObject[3] = paramByte4;
    localObject[4] = paramByte5;
    if (paramInt != 0)
    {
      localObject[5] = ((byte)(paramInt & 0xFF));
      localObject[6] = ((byte)(paramInt >> 8));
    }
    return m.a(SppProc.CMD_TYPE.SPP_SEND_NET_TEMP_DISP_INFO, (byte[])localObject);
  }
  
  public static byte[] a(float paramFloat)
  {
    int k = (int)(paramFloat * 10.0F);
    int i = (byte)(k % 100);
    int j = (byte)(k / 100);
    return m.a(SppProc.CMD_TYPE.SPP_SET_FM_CURRENT_FREQ, new byte[] { i, j });
  }
  
  public static byte[] a(float paramFloat, boolean paramBoolean)
  {
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("------------->freq=");
    ((StringBuilder)localObject).append(paramFloat);
    ((StringBuilder)localObject).append("   is_favourite=");
    ((StringBuilder)localObject).append(paramBoolean);
    com.divoom.Divoom.utils.d.c(((StringBuilder)localObject).toString());
    localObject = new byte[3];
    int i = (int)(paramFloat * 10.0F);
    if (paramBoolean == true) {
      localObject[0] = 1;
    } else {
      localObject[0] = 0;
    }
    localObject[1] = ((byte)(i % 100));
    localObject[2] = ((byte)(i / 100));
    return m.a(SppProc.CMD_TYPE.SPP_SET_FM_FAVOURITE, (byte[])localObject);
  }
  
  public static byte[] a(int paramInt)
  {
    int i = (byte)paramInt;
    return m.a(SppProc.CMD_TYPE.SPP_SEND_LED_WORD_CMD, new byte[] { 0, i });
  }
  
  public static byte[] a(int paramInt1, int paramInt2, int paramInt3)
  {
    org.greenrobot.eventbus.c.a().d(new com.divoom.Divoom.b.h.c());
    int i = (byte)(paramInt1 & 0xFF);
    int j = (byte)(paramInt2 & 0xFF);
    int k = (byte)(paramInt2 >> 8 & 0xFF);
    int m = (byte)(paramInt3 & 0xFF);
    int n = (byte)(paramInt3 >> 8 & 0xFF);
    return m.a(SppProc.CMD_TYPE.SPP_SET_USER_GIF, new byte[] { 0, 3, i, j, k, m, n });
  }
  
  public static byte[] a(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean)
  {
    byte[] arrayOfByte = new byte[10];
    arrayOfByte[0] = ((byte)LightEnum.LIGHT_MODE._value);
    arrayOfByte[1] = ((byte)(paramInt1 & 0xFF));
    arrayOfByte[2] = ((byte)(paramInt2 & 0xFF));
    arrayOfByte[3] = ((byte)(paramInt3 & 0xFF));
    arrayOfByte[4] = ((byte)(paramInt4 & 0xFF));
    arrayOfByte[5] = ((byte)(paramInt5 & 0xFF));
    arrayOfByte[6] = ((byte)paramBoolean);
    return m.a(SppProc.CMD_TYPE.SPP_SET_BOX_MODE, arrayOfByte);
  }
  
  public static byte[] a(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte = new byte[10];
    int i = (byte)LightEnum.ENV_MODE._value;
    int j = 0;
    arrayOfByte[0] = i;
    arrayOfByte[1] = ((byte)(paramInt1 & 0xFF));
    arrayOfByte[2] = ((byte)(paramInt5 & 0xFF));
    paramInt1 = 3;
    paramInt5 = j;
    while (paramInt5 < paramArrayOfByte.length)
    {
      arrayOfByte[paramInt1] = paramArrayOfByte[paramInt5];
      paramInt5 += 1;
      paramInt1 += 1;
    }
    paramInt5 = paramInt1 + 1;
    arrayOfByte[paramInt1] = ((byte)(paramInt2 & 0xFF));
    arrayOfByte[paramInt5] = ((byte)(paramInt3 & 0xFF));
    arrayOfByte[(paramInt5 + 1)] = ((byte)(paramInt4 & 0xFF));
    return m.a(SppProc.CMD_TYPE.SPP_SET_BOX_MODE, arrayOfByte);
  }
  
  public static byte[] a(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i = (byte)paramInt1;
    int j = (byte)paramInt2;
    int k = (byte)paramBoolean;
    return m.a(SppProc.CMD_TYPE.SPP_SET_MIX_MUISE_MODE, new byte[] { i, j, k });
  }
  
  public static byte[] a(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
  {
    paramArrayOfByte = com.divoom.Divoom.led.b.a(paramArrayOfByte);
    if ((paramArrayOfByte != null) && (paramArrayOfByte.length != 0))
    {
      byte[] arrayOfByte = new byte[paramArrayOfByte.length + 5];
      arrayOfByte[0] = ((byte)(paramInt1 & 0xFF));
      arrayOfByte[1] = ((byte)(paramInt2 & 0xFF));
      arrayOfByte[2] = ((byte)(paramInt2 >> 8 & 0xFF));
      arrayOfByte[3] = ((byte)(paramArrayOfByte.length & 0xFF));
      arrayOfByte[4] = ((byte)(paramArrayOfByte.length >> 8 & 0xFF));
      System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 5, paramArrayOfByte.length);
      return m.a(SppProc.CMD_TYPE.SPP_DRAWING_MUL_ENCODE_MOVIE_PLAY, arrayOfByte);
    }
    return null;
  }
  
  public static byte[] a(int paramInt, u paramu)
  {
    int j = paramu.e;
    int k = 5;
    byte[] arrayOfByte = new byte[j + 5];
    int i = (byte)paramInt;
    j = 0;
    arrayOfByte[0] = i;
    arrayOfByte[1] = ((byte)paramu.a);
    arrayOfByte[2] = ((byte)paramu.b);
    arrayOfByte[3] = ((byte)paramu.c);
    if (paramu.e == 0)
    {
      arrayOfByte[4] = ((byte)paramu.e);
    }
    else
    {
      arrayOfByte[4] = ((byte)paramu.e);
      paramInt = k;
      while (j < paramu.d.length)
      {
        arrayOfByte[paramInt] = ((byte)paramu.d[j]);
        j += 1;
        paramInt += 1;
      }
    }
    return m.a(SppProc.CMD_TYPE.SPP_DRAWING_MUL_PAD_CTRL, arrayOfByte);
  }
  
  public static byte[] a(int paramInt, byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte = new byte[paramArrayOfByte.length + 2];
    arrayOfByte[0] = ((byte)(paramInt & 0xFF));
    arrayOfByte[1] = ((byte)(paramInt >> 8 & 0xFF));
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 2, paramArrayOfByte.length);
    return m.a(SppProc.CMD_TYPE.SPP_APP_SEND_FILE_DATA, arrayOfByte);
  }
  
  public static byte[] a(u paramu)
  {
    int k = paramu.e;
    int j = 4;
    byte[] arrayOfByte = new byte[k + 4];
    int i = (byte)paramu.a;
    k = 0;
    arrayOfByte[0] = i;
    arrayOfByte[1] = ((byte)paramu.b);
    arrayOfByte[2] = ((byte)paramu.c);
    if (paramu.e == 0)
    {
      arrayOfByte[3] = ((byte)paramu.e);
    }
    else
    {
      arrayOfByte[3] = ((byte)paramu.e);
      while (k < paramu.d.length)
      {
        arrayOfByte[j] = ((byte)paramu.d[k]);
        k += 1;
        j += 1;
      }
    }
    return m.a(SppProc.CMD_TYPE.SPP_DRAWING_PAD_CTRL, arrayOfByte);
  }
  
  public static byte[] a(com.divoom.Divoom.b.i.b paramb)
  {
    Object localObject1 = GlobalApplication.BlueModeEnum.getMode();
    Object localObject2 = GlobalApplication.BlueModeEnum.OldMode;
    int k = 0;
    if (localObject1 == localObject2)
    {
      localObject1 = new byte['Ǵ'];
      localObject1[0] = ((byte)paramb.a);
      localObject1[1] = ((byte)paramb.b);
      localObject1[2] = ((byte)paramb.c);
      localObject1[3] = ((byte)paramb.d);
      localObject1[4] = ((byte)paramb.e);
      localObject1[5] = ((byte)paramb.f);
      localObject1[6] = ((byte)paramb.g);
      localObject1[7] = ((byte)paramb.h);
      localObject1[8] = ((byte)paramb.i);
      localObject1[9] = ((byte)paramb.j);
      localObject2 = new byte['¶'];
      i = 0;
      int j = 0;
      while (i < 363)
      {
        if (i == 362) {
          localObject2[j] = ((byte)(j(paramb.k[i]) >> 4));
        } else {
          localObject2[j] = ((byte)(j(paramb.k[i]) >> 4 | j(paramb.k[(i + 1)]) >> 4 << 4));
        }
        j += 1;
        i += 2;
      }
      paramb = GlobalApplication.b().g().picDecodeEleven((byte[])localObject2);
      localObject2 = new byte[10 + paramb.length];
      j = 0;
      i = k;
      while (i < localObject2.length)
      {
        if (i > 9)
        {
          localObject2[i] = paramb[j];
          j += 1;
        }
        else
        {
          localObject2[i] = localObject1[i];
        }
        i += 1;
      }
      return m.a(SppProc.CMD_TYPE.SPP_SET_TIME_MANAGE_INFO, (byte[])localObject2);
    }
    localObject1 = new byte['Ϩ'];
    localObject1[0] = ((byte)paramb.a);
    localObject1[1] = ((byte)paramb.b);
    localObject1[2] = ((byte)paramb.c);
    localObject1[3] = ((byte)paramb.d);
    localObject1[4] = ((byte)paramb.e);
    localObject1[5] = ((byte)paramb.f);
    localObject1[6] = ((byte)paramb.g);
    localObject1[7] = ((byte)paramb.h);
    localObject1[8] = ((byte)paramb.i);
    localObject1[9] = ((byte)paramb.j);
    paramb = GlobalApplication.b().g().PixelEncodeSixteen(paramb.k, 1, 0);
    localObject1[10] = ((byte)(paramb.length & 0xFF));
    localObject1[11] = ((byte)((paramb.length & 0xFF00) >> 8));
    System.arraycopy(paramb, 0, localObject1, 12, paramb.length);
    int i = 12 + paramb.length;
    paramb = new byte[i];
    System.arraycopy(localObject1, 0, paramb, 0, i);
    return m.a(SppProc.CMD_TYPE.SPP_SET_TIME_MANAGE_INFO, paramb);
  }
  
  public static byte[] a(NetTemp paramNetTemp)
  {
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("------------>发送网络数据给设备  temp.getNum()=");
    byte[] arrayOfByte = paramNetTemp.getNum();
    int j = 0;
    boolean bool;
    if (arrayOfByte == null) {
      bool = true;
    } else {
      bool = false;
    }
    ((StringBuilder)localObject).append(bool);
    ((StringBuilder)localObject).append("组数=");
    ((StringBuilder)localObject).append(paramNetTemp.getNum().length / 2);
    com.divoom.Divoom.utils.d.c(((StringBuilder)localObject).toString());
    localObject = new byte[paramNetTemp.getNum().length + 10];
    localObject[0] = ((byte)(paramNetTemp.getYear() & 0xFF));
    localObject[1] = ((byte)(paramNetTemp.getYear() >> 8));
    localObject[2] = paramNetTemp.getMon();
    localObject[3] = paramNetTemp.getDay();
    localObject[4] = paramNetTemp.getHour();
    localObject[5] = paramNetTemp.getMin();
    int i = 7;
    localObject[6] = ((byte)(paramNetTemp.getNum().length / 2));
    while (j < paramNetTemp.getNum().length)
    {
      localObject[i] = paramNetTemp.getNum()[j];
      j += 1;
      i += 1;
    }
    return m.a(SppProc.CMD_TYPE.SPP_SEND_NET_TEMP_INFO, (byte[])localObject);
  }
  
  public static byte[] a(MultiModeEnum paramMultiModeEnum)
  {
    int i = (byte)MultiControlEnum.mirrorOrfit._value;
    int j = (byte)paramMultiModeEnum._value;
    return m.a(SppProc.CMD_TYPE.SPP_SET_MUL_DEVICE_CTRL, new byte[] { i, j });
  }
  
  public static byte[] a(MultiShowEnum paramMultiShowEnum)
  {
    int i = (byte)MultiControlEnum.setMultiMode._value;
    int j = (byte)paramMultiShowEnum._value;
    return m.a(SppProc.CMD_TYPE.SPP_SET_MUL_DEVICE_CTRL, new byte[] { i, j });
  }
  
  public static byte[] a(SppProc.LIGHT_MODE paramLIGHT_MODE)
  {
    int i = paramLIGHT_MODE.getCmd_data()[0];
    return m.a(SppProc.CMD_TYPE.SPP_SET_BOX_MODE, new byte[] { i, 0, 0, 0, 0, 0 });
  }
  
  public static byte[] a(SppProc.LIGHT_MODE paramLIGHT_MODE, byte paramByte)
  {
    int i = paramLIGHT_MODE.getCmd_data()[0];
    return m.a(SppProc.CMD_TYPE.SPP_SET_BOX_MODE, new byte[] { i, paramByte });
  }
  
  public static byte[] a(SppProc.LIGHT_MODE paramLIGHT_MODE, byte paramByte1, byte paramByte2, byte paramByte3, byte paramByte4)
  {
    byte[] arrayOfByte = new byte[6];
    arrayOfByte[0] = paramLIGHT_MODE.getCmd_data()[0];
    arrayOfByte[1] = paramByte1;
    arrayOfByte[2] = paramByte2;
    arrayOfByte[3] = paramByte3;
    arrayOfByte[4] = paramByte4;
    return m.a(SppProc.CMD_TYPE.SPP_SET_BOX_MODE, arrayOfByte);
  }
  
  public static byte[] a(SppProc.LIGHT_MODE paramLIGHT_MODE, byte paramByte1, byte paramByte2, byte paramByte3, byte paramByte4, byte paramByte5)
  {
    int i = paramLIGHT_MODE.getCmd_data()[0];
    return m.a(SppProc.CMD_TYPE.SPP_SET_BOX_MODE, new byte[] { i, paramByte1, paramByte2, paramByte3, paramByte4, paramByte5 });
  }
  
  public static byte[] a(SppProc.LIGHT_MODE paramLIGHT_MODE, byte paramByte1, byte paramByte2, byte paramByte3, byte paramByte4, byte paramByte5, byte paramByte6, byte paramByte7)
  {
    int i = paramLIGHT_MODE.getCmd_data()[0];
    return m.a(SppProc.CMD_TYPE.SPP_SET_BOX_MODE, new byte[] { i, paramByte1, paramByte2, paramByte3, paramByte4, paramByte5, paramByte6, paramByte7 });
  }
  
  public static byte[] a(SppProc.WORK_MODE paramWORK_MODE)
  {
    byte[] arrayOfByte = new byte[1];
    arrayOfByte[0] = 0;
    arrayOfByte[0] = paramWORK_MODE.value();
    return m.a(SppProc.CMD_TYPE.SPP_CHANGE_MODE, arrayOfByte);
  }
  
  public static byte[] a(com.divoom.Divoom.bluetooth.a.d paramd)
  {
    byte[] arrayOfByte = new byte[16];
    arrayOfByte[0] = ((byte)(paramd.a() & 0xFF));
    arrayOfByte[1] = ((byte)(paramd.a() >> 8 & 0xFF));
    arrayOfByte[2] = ((byte)(paramd.a() >> 16 & 0xFF));
    arrayOfByte[3] = ((byte)(paramd.a() >> 24 & 0xFF));
    arrayOfByte[4] = ((byte)(int)(paramd.f() & 0xFF));
    arrayOfByte[5] = ((byte)(int)(paramd.f() >> 8 & 0xFF));
    arrayOfByte[6] = ((byte)(int)(paramd.f() >> 16 & 0xFF));
    arrayOfByte[7] = ((byte)(int)(0xFF & paramd.f() >> 24));
    System.arraycopy(paramd.g(), 0, arrayOfByte, 8, 4);
    arrayOfByte[12] = ((byte)(paramd.e() & 0xFF));
    arrayOfByte[13] = ((byte)(paramd.e() >> 8 & 0xFF));
    arrayOfByte[14] = ((byte)(paramd.e() >> 16 & 0xFF));
    arrayOfByte[15] = ((byte)(paramd.e() >> 24 & 0xFF));
    return m.a(SppProc.CMD_TYPE.SPP_HOT_UPDATE_FILE_INFO, arrayOfByte);
  }
  
  public static byte[] a(com.divoom.Divoom.bluetooth.alarm.a parama)
  {
    l.c("", "设置闹钟--------》");
    byte[] arrayOfByte = new byte[100];
    int i = parama.a;
    int j = 0;
    arrayOfByte[0] = i;
    if (parama.b == true) {
      arrayOfByte[1] = 1;
    } else {
      arrayOfByte[1] = 0;
    }
    arrayOfByte[2] = parama.c;
    arrayOfByte[3] = parama.d;
    arrayOfByte[4] = parama.e;
    arrayOfByte[5] = parama.f;
    arrayOfByte[6] = parama.g;
    int k = (int)(parama.h * 10.0F);
    arrayOfByte[7] = ((byte)(k % 100));
    arrayOfByte[8] = ((byte)(k / 100));
    arrayOfByte[9] = parama.i;
    parama = new byte[10];
    while (j < 10)
    {
      parama[j] = arrayOfByte[j];
      j += 1;
    }
    return m.a(SppProc.CMD_TYPE.SPP_SET_ALARM_TIME_SCENE, parama);
  }
  
  public static byte[] a(String paramString)
  {
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Set Device Name ");
    ((StringBuilder)localObject).append(paramString);
    l.c("octopus.CmdManager", ((StringBuilder)localObject).toString());
    paramString = paramString.getBytes();
    int i = (byte)paramString.length;
    localObject = new byte[paramString.length + 1];
    localObject[0] = i;
    System.arraycopy(paramString, 0, localObject, 1, paramString.length);
    return m.a(SppProc.CMD_TYPE.SPP_SET_DEVICE_NAME, (byte[])localObject);
  }
  
  public static byte[] a(String paramString, int paramInt)
  {
    org.greenrobot.eventbus.c.a().d(new com.divoom.Divoom.b.h.c());
    byte[] arrayOfByte = new byte[paramString.length() * 2 + 4];
    arrayOfByte[0] = 0;
    arrayOfByte[1] = 1;
    arrayOfByte[2] = ((byte)(paramInt & 0xFF));
    arrayOfByte[3] = ((byte)(paramString.length() & 0xFF));
    if (paramString.length() != 0) {
      try
      {
        paramString = paramString.getBytes("UTF-16LE");
        System.arraycopy(paramString, 0, arrayOfByte, 4, paramString.length);
      }
      catch (UnsupportedEncodingException paramString)
      {
        paramString.printStackTrace();
      }
    }
    return m.a(SppProc.CMD_TYPE.SPP_SET_USER_GIF, arrayOfByte);
  }
  
  public static byte[] a(short paramShort)
  {
    int i = (byte)(paramShort & 0xFF);
    int j = (byte)(paramShort >> 8 & 0xFF);
    return m.a(SppProc.CMD_TYPE.SPP_SET_AUTO_POWER_OFF, new byte[] { i, j });
  }
  
  public static byte[] a(boolean paramBoolean)
  {
    int i = (byte)paramBoolean;
    return m.a(SppProc.CMD_TYPE.SPP_SET_SOUND_CTRL, new byte[] { i });
  }
  
  public static byte[] a(boolean paramBoolean, byte paramByte)
  {
    byte[] arrayOfByte = new byte[2];
    if (paramBoolean) {
      arrayOfByte[0] = 1;
    } else {
      arrayOfByte[0] = 0;
    }
    arrayOfByte[1] = paramByte;
    return m.a(SppProc.CMD_TYPE.SPP_SET_GAME, arrayOfByte);
  }
  
  public static byte[] a(boolean paramBoolean, byte paramByte1, byte paramByte2)
  {
    int i = (byte)paramBoolean;
    return m.a(SppProc.CMD_TYPE.SPP_SET_SCENE_LISTEN, new byte[] { i, paramByte1, paramByte2 });
  }
  
  public static byte[] a(boolean paramBoolean, int paramInt1, int paramInt2)
  {
    org.greenrobot.eventbus.c.a().d(new com.divoom.Divoom.b.h.c());
    byte[] arrayOfByte = new byte[6];
    arrayOfByte[0] = 1;
    arrayOfByte[1] = ((byte)paramBoolean);
    arrayOfByte[2] = ((byte)(paramInt1 & 0xFF));
    arrayOfByte[3] = ((byte)((paramInt1 & 0xFF00) >> 8));
    arrayOfByte[4] = ((byte)(paramInt2 & 0xFF));
    arrayOfByte[5] = ((byte)((paramInt2 & 0xFF00) >> 8));
    l.c("octopus.CmdManager", aw.a(arrayOfByte));
    return m.a(SppProc.CMD_TYPE.SPP_SET_TOOL_INFO, arrayOfByte);
  }
  
  public static byte[] a(byte[] paramArrayOfByte)
  {
    paramArrayOfByte = com.divoom.Divoom.led.b.a(paramArrayOfByte);
    if (paramArrayOfByte == null) {
      return null;
    }
    byte[] arrayOfByte = new byte[paramArrayOfByte.length + 3];
    arrayOfByte[0] = 1;
    arrayOfByte[1] = ((byte)(paramArrayOfByte.length & 0xFF));
    arrayOfByte[2] = ((byte)(paramArrayOfByte.length >> 8 & 0xFF));
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 3, paramArrayOfByte.length);
    return m.a(SppProc.CMD_TYPE.SPP_SET_USER_GIF, arrayOfByte);
  }
  
  public static byte[] a(byte[] paramArrayOfByte, int paramInt, long paramLong)
  {
    byte[] arrayOfByte = new byte['Ǵ'];
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("getAnimationCmd ");
    localStringBuilder.append(paramArrayOfByte.length);
    localStringBuilder.append("; seq ");
    paramInt -= 1;
    localStringBuilder.append(paramInt);
    localStringBuilder.append("; int ");
    localStringBuilder.append(paramLong);
    l.c("octopus.CmdManager", localStringBuilder.toString());
    int j = 0;
    arrayOfByte[0] = 0;
    arrayOfByte[1] = 10;
    arrayOfByte[2] = 10;
    arrayOfByte[3] = 4;
    arrayOfByte[4] = ((byte)(paramInt & 0xFF));
    arrayOfByte[5] = ((byte)(int)(paramLong / 100L & 0xFF));
    l.c("octopus.CmdManager", aw.a(paramArrayOfByte));
    int i = 0;
    paramInt = 6;
    while (i < 363)
    {
      if (i == 362) {
        arrayOfByte[paramInt] = ((byte)(j(paramArrayOfByte[i]) >> 4));
      } else {
        arrayOfByte[paramInt] = ((byte)(j(paramArrayOfByte[i]) >> 4 | j(paramArrayOfByte[(i + 1)]) >> 4 << 4));
      }
      paramInt += 1;
      i += 2;
    }
    paramArrayOfByte = new byte[paramInt];
    i = j;
    while (i < paramInt)
    {
      paramArrayOfByte[i] = arrayOfByte[i];
      i += 1;
    }
    return m.a(SppProc.CMD_TYPE.SPP_SET_MUL_BOX_COLOR, paramArrayOfByte);
  }
  
  public static List<byte[]> b(byte paramByte1, byte paramByte2, int paramInt, List<byte[]> paramList)
  {
    ArrayList localArrayList = new ArrayList();
    Object localObject;
    if (GlobalApplication.BlueModeEnum.getMode() == GlobalApplication.BlueModeEnum.OldMode)
    {
      b1 = 0;
      while (b1 < paramByte2)
      {
        arrayOfByte = new byte['Ǵ'];
        arrayOfByte[0] = paramByte1;
        arrayOfByte[1] = paramByte2;
        arrayOfByte[2] = ((byte)(b1 & 0xFF));
        arrayOfByte[3] = ((byte)(paramInt / 100 & 0xFF));
        i = 0;
        b2 = 4;
        while (i < 363)
        {
          if (i == 362) {
            arrayOfByte[b2] = ((byte)(j(((byte[])paramList.get(b1))[i]) >> 4));
          } else {
            arrayOfByte[b2] = ((byte)(j(((byte[])paramList.get(b1))[i]) >> 4 | j(((byte[])paramList.get(b1))[(i + 1)]) >> 4 << 4));
          }
          b2 += 1;
          i += 2;
        }
        localObject = new byte[b2];
        i = 0;
        while (i < b2)
        {
          localObject[i] = arrayOfByte[i];
          i += 1;
        }
        localArrayList.add(m.a(SppProc.CMD_TYPE.SPP_SET_DIALY_TIME_GIF, (byte[])localObject));
        b1 += 1;
      }
    }
    byte[] arrayOfByte = new byte[((byte[])paramList.get(0)).length * paramByte2];
    byte b1 = 0;
    byte b2 = 0;
    while (b1 < paramList.size())
    {
      System.arraycopy(paramList.get(b1), 0, arrayOfByte, b2, ((byte[])paramList.get(b1)).length);
      b2 += ((byte[])paramList.get(b1)).length;
      b1 += 1;
    }
    paramList = GlobalApplication.b().g().PixelEncodeSixteen(arrayOfByte, paramByte2, paramInt);
    int i = paramList.length;
    int j = (int)Math.ceil(i / 200.0F);
    paramInt = 0;
    b1 = 0;
    while (paramInt < j)
    {
      if (paramInt != j - 1) {
        b2 = 200;
      } else {
        b2 = i - paramInt * 200;
      }
      arrayOfByte = new byte[b2 + 4];
      arrayOfByte[0] = paramByte1;
      arrayOfByte[1] = ((byte)(i & 0xFF));
      arrayOfByte[2] = ((byte)(i >> 8 & 0xFF));
      arrayOfByte[3] = ((byte)paramInt);
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("ret ");
      ((StringBuilder)localObject).append(paramList.length);
      ((StringBuilder)localObject).append(" ret_offset ");
      ((StringBuilder)localObject).append(b1);
      ((StringBuilder)localObject).append(" packet ");
      ((StringBuilder)localObject).append(arrayOfByte.length);
      l.c("octopus.CmdManager", ((StringBuilder)localObject).toString());
      System.arraycopy(paramList, b1, arrayOfByte, 4, b2);
      b1 += b2;
      localArrayList.add(m.a(SppProc.CMD_TYPE.SPP_SET_DIALY_TIME_GIF, arrayOfByte));
      paramInt += 1;
    }
    return localArrayList;
  }
  
  public static List<byte[]> b(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    paramArrayOfByte = GlobalApplication.b().g().PixelEncodeSixteen(paramArrayOfByte, paramInt1, paramInt2);
    int j = paramArrayOfByte.length;
    int k = (int)Math.ceil(j / 200.0F);
    ArrayList localArrayList = new ArrayList();
    paramInt1 = 0;
    paramInt2 = 0;
    while (paramInt1 < k)
    {
      int i;
      if (paramInt1 != k - 1) {
        i = 200;
      } else {
        i = j - paramInt1 * 200;
      }
      byte[] arrayOfByte = new byte[i + 3];
      arrayOfByte[0] = ((byte)(j & 0xFF));
      arrayOfByte[1] = ((byte)(j >> 8 & 0xFF));
      arrayOfByte[2] = ((byte)paramInt1);
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("ret ");
      localStringBuilder.append(paramArrayOfByte.length);
      localStringBuilder.append(" ret_offset ");
      localStringBuilder.append(paramInt2);
      localStringBuilder.append(" packet ");
      localStringBuilder.append(arrayOfByte.length);
      l.c("octopus.CmdManager", localStringBuilder.toString());
      System.arraycopy(paramArrayOfByte, paramInt2, arrayOfByte, 3, i);
      paramInt2 += i;
      localArrayList.add(m.a(SppProc.CMD_TYPE.SPP_SET_DIVOOM_LEAVE_MSG_GIF, arrayOfByte));
      paramInt1 += 1;
    }
    return localArrayList;
  }
  
  public static void b(int paramInt1, int paramInt2)
  {
    int i = (byte)paramInt1;
    int j = (byte)paramInt2;
    byte[] arrayOfByte = m.a(SppProc.CMD_TYPE.SPP_SET_NEW_MIX_MUSIC_MODE, new byte[] { i, j });
    k.a().b(arrayOfByte);
  }
  
  @SuppressLint({"CheckResult"})
  public static void b(String paramString)
  {
    h.a(Integer.valueOf(1)).a(io.reactivex.e.a.b()).b(new e()
    {
      public void a(Integer paramAnonymousInteger)
        throws Exception
      {
        CmdManager.c(this.a);
      }
    });
  }
  
  public static byte[] b()
  {
    org.greenrobot.eventbus.c.a().d(new com.divoom.Divoom.b.h.c());
    return m.a(SppProc.CMD_TYPE.SPP_SET_USER_GIF, new byte[] { 0, 2 });
  }
  
  public static byte[] b(byte paramByte)
  {
    return m.a(SppProc.CMD_TYPE.SPP_SET_SLEEP_LIGHT, new byte[] { paramByte });
  }
  
  public static byte[] b(byte paramByte1, byte paramByte2)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("------发送实时温度------->netTemp=");
    localStringBuilder.append(paramByte1);
    localStringBuilder.append("  typeDemo=");
    localStringBuilder.append(paramByte2);
    com.divoom.Divoom.utils.d.c(localStringBuilder.toString());
    return m.a(SppProc.CMD_TYPE.SPP_SEND_CUR_NET_TEMP, new byte[] { paramByte1, paramByte2 });
  }
  
  public static byte[] b(int paramInt)
  {
    int i = (byte)paramInt;
    return m.a(SppProc.CMD_TYPE.SPP_GET_FILE_VERSION, new byte[] { i });
  }
  
  public static byte[] b(int paramInt, byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte = new byte[paramArrayOfByte.length + 2];
    arrayOfByte[0] = ((byte)(paramInt & 0xFF));
    arrayOfByte[1] = ((byte)(paramInt >> 8 & 0xFF));
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 2, paramArrayOfByte.length);
    return m.a(SppProc.CMD_TYPE.SPP_HOT_SEND_FILE_DATA, arrayOfByte);
  }
  
  public static byte[] b(List<com.divoom.Divoom.bluetooth.a.d> paramList)
  {
    int j = paramList.size();
    int i = 1;
    byte[] arrayOfByte = new byte[j * 8 + 1];
    arrayOfByte[0] = ((byte)(paramList.size() & 0xFF));
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      com.divoom.Divoom.bluetooth.a.d locald = (com.divoom.Divoom.bluetooth.a.d)paramList.next();
      j = i + 1;
      arrayOfByte[i] = ((byte)(locald.a() & 0xFF));
      i = j + 1;
      arrayOfByte[j] = ((byte)(locald.a() >> 8 & 0xFF));
      j = i + 1;
      arrayOfByte[i] = ((byte)(locald.a() >> 16 & 0xFF));
      i = j + 1;
      arrayOfByte[j] = ((byte)(locald.a() >> 24 & 0xFF));
      j = i + 1;
      arrayOfByte[i] = ((byte)(locald.e() & 0xFF));
      i = j + 1;
      arrayOfByte[j] = ((byte)(locald.e() >> 8 & 0xFF));
      j = i + 1;
      arrayOfByte[i] = ((byte)(locald.e() >> 16 & 0xFF));
      i = j + 1;
      arrayOfByte[j] = ((byte)(locald.e() >> 24 & 0xFF));
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("sendHotFileList ");
      localStringBuilder.append(locald.toString());
      l.c("octopus.CmdManager", localStringBuilder.toString());
    }
    return m.a(SppProc.CMD_TYPE.SPP_SEND_HOT_FILE_LIST, arrayOfByte);
  }
  
  public static byte[] b(boolean paramBoolean)
  {
    int i = (byte)paramBoolean;
    return m.a(SppProc.CMD_TYPE.SPP_SET_ENERGY_CTRL, new byte[] { i });
  }
  
  public static byte[] b(boolean paramBoolean, byte paramByte)
  {
    byte[] arrayOfByte = new byte[2];
    if (paramBoolean) {
      arrayOfByte[0] = 1;
    } else {
      arrayOfByte[0] = 0;
    }
    arrayOfByte[1] = paramByte;
    return m.a(SppProc.CMD_TYPE.SPP_SET_TALK, arrayOfByte);
  }
  
  public static byte[] b(boolean paramBoolean, byte paramByte1, byte paramByte2)
  {
    int i = (byte)paramBoolean;
    return m.a(SppProc.CMD_TYPE.SPP_SET_ALARM_LISTEN, new byte[] { i, paramByte1, paramByte2 });
  }
  
  public static byte[] b(boolean paramBoolean, int paramInt1, int paramInt2)
  {
    org.greenrobot.eventbus.c.a().d(new com.divoom.Divoom.b.h.c());
    byte[] arrayOfByte = new byte[4];
    arrayOfByte[0] = 3;
    arrayOfByte[1] = ((byte)paramBoolean);
    arrayOfByte[2] = ((byte)(paramInt1 & 0xFF));
    arrayOfByte[3] = ((byte)(paramInt2 & 0xFF));
    l.c("octopus.CmdManager", aw.a(arrayOfByte));
    return m.a(SppProc.CMD_TYPE.SPP_SET_TOOL_INFO, arrayOfByte);
  }
  
  public static byte[] b(byte[] paramArrayOfByte)
  {
    return m.a(SppProc.CMD_TYPE.SPP_SET_SLEEP_TIME, paramArrayOfByte);
  }
  
  public static void c(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return;
    }
    Object localObject = null;
    try
    {
      w localw = new w(GlobalApplication.b().i());
      localObject = localw;
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
    int i3 = paramString.length();
    byte[] arrayOfByte1 = ((w)localObject).a(paramString);
    localObject = ((w)localObject).b(paramString);
    l.c("octopus.CmdManager", "fontInfo");
    aw.c((byte[])localObject);
    int k = 0;
    int i = 0;
    int m;
    for (int j = 0;; j = m)
    {
      m = i3 / 5;
      if (k >= m + 1) {
        break;
      }
      if (k != m) {
        m = 5;
      } else {
        m = i3 % 5;
      }
      int i1 = 3;
      byte[] arrayOfByte2 = new byte[2 * m + 3 + 32 * m];
      arrayOfByte2[0] = ax.b(i3);
      arrayOfByte2[1] = ax.b(k * 5);
      arrayOfByte2[2] = ax.b(m);
      int n = j;
      j = i;
      int i2 = 0;
      i = n;
      n = i2;
      while (n < m)
      {
        System.arraycopy(arrayOfByte1, j, arrayOfByte2, i1, 2);
        j += 2;
        i1 += 2;
        System.arraycopy(localObject, i, arrayOfByte2, i1, 32);
        i += 32;
        i1 += 32;
        n += 1;
      }
      aw.a(arrayOfByte2, 16);
      arrayOfByte2 = m.a(SppProc.CMD_TYPE.SPP_LED_UPDATE_FONT_INFO, arrayOfByte2);
      k.a().a(arrayOfByte2, true);
      k += 1;
      m = i;
      i = j;
    }
    d(paramString);
  }
  
  public static byte[] c()
  {
    org.greenrobot.eventbus.c.a().d(new com.divoom.Divoom.b.h.c());
    return m.a(SppProc.CMD_TYPE.SPP_SET_USER_GIF, new byte[] { 2 });
  }
  
  public static byte[] c(byte paramByte)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("setPlayVoice-------------------->");
    localStringBuilder.append(paramByte);
    LogUtil.e(localStringBuilder.toString());
    localStringBuilder = new StringBuilder();
    localStringBuilder.append("0:开始录音, 1:开始播放录音, 2:停止播放录音");
    localStringBuilder.append(paramByte);
    com.divoom.Divoom.utils.d.a(localStringBuilder.toString());
    return m.a(SppProc.CMD_TYPE.SPP_SET_PLAY_STOP_VOICE, new byte[] { paramByte });
  }
  
  public static byte[] c(int paramInt)
  {
    int i = (byte)paramInt;
    return m.a(SppProc.CMD_TYPE.SPP_SEND_HOTCTRL, new byte[] { i });
  }
  
  public static byte[] c(int paramInt1, int paramInt2)
  {
    int i = (byte)MultiControlEnum.exchangeMulti._value;
    int j = (byte)paramInt1;
    int k = (byte)paramInt2;
    return m.a(SppProc.CMD_TYPE.SPP_SET_MUL_DEVICE_CTRL, new byte[] { i, j, k });
  }
  
  public static byte[] c(int paramInt, byte[] paramArrayOfByte)
  {
    org.greenrobot.eventbus.c.a().d(new com.divoom.Divoom.b.h.c());
    paramArrayOfByte = GlobalApplication.b().g().PixelEncodeSixteen(paramArrayOfByte, 1, 0);
    byte[] arrayOfByte = new byte[paramArrayOfByte.length + 1 + 2];
    arrayOfByte[0] = ((byte)(paramInt & 0xFF));
    arrayOfByte[1] = ((byte)(paramArrayOfByte.length & 0xFF));
    arrayOfByte[2] = ((byte)(paramArrayOfByte.length >> 8 & 0xFF));
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 3, paramArrayOfByte.length);
    return m.a(SppProc.CMD_TYPE.SPP_DRAWING_ENCODE_PIC, arrayOfByte);
  }
  
  public static byte[] c(boolean paramBoolean)
  {
    int j = 1;
    if (paramBoolean != true) {
      j = 0;
    }
    int i = (byte)j;
    return m.a(SppProc.CMD_TYPE.SPP_SET_TEMP_TYPE, new byte[] { i });
  }
  
  public static byte[] c(byte[] paramArrayOfByte)
  {
    return m.a(SppProc.CMD_TYPE.SPP_SET_SLEEP_SCENE, paramArrayOfByte);
  }
  
  @SuppressLint({"CheckResult"})
  public static void d(String paramString)
  {
    if (paramString == null) {
      return;
    }
    byte[] arrayOfByte2 = new byte[paramString.length() * 2 + 2];
    arrayOfByte2[0] = 1;
    arrayOfByte2[1] = ((byte)(paramString.length() & 0xFF));
    if (paramString.length() != 0)
    {
      byte[] arrayOfByte1 = new byte[2];
      try
      {
        paramString = paramString.getBytes("UTF-16LE");
      }
      catch (UnsupportedEncodingException paramString)
      {
        paramString.printStackTrace();
        paramString = arrayOfByte1;
      }
      System.arraycopy(paramString, 0, arrayOfByte2, 2, paramString.length);
    }
    paramString = m.a(SppProc.CMD_TYPE.SPP_SEND_LED_WORD_CMD, arrayOfByte2);
    k.a().a(paramString, true);
  }
  
  public static byte[] d()
  {
    return m.a(SppProc.CMD_TYPE.SPP_GET_CONNECTED_FLAG, null);
  }
  
  public static byte[] d(byte paramByte)
  {
    return m.a(SppProc.CMD_TYPE.SPP_SET_SCENE_LISTEN_VOLUME, new byte[] { paramByte });
  }
  
  public static byte[] d(int paramInt)
  {
    int i = (byte)paramInt;
    return m.a(SppProc.CMD_TYPE.SPP_SET_TOOL_INFO, new byte[] { 2, i });
  }
  
  public static byte[] d(int paramInt1, int paramInt2)
  {
    int i = (byte)(paramInt1 & 0xFF);
    int j = (byte)(paramInt1 >> 8 & 0xFF);
    int k = (byte)(paramInt2 & 0xFF);
    int m = (byte)(paramInt2 >> 8 & 0xFF);
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("开始获取SD列表 startId ");
    localStringBuilder.append(paramInt1);
    localStringBuilder.append(", end ");
    localStringBuilder.append(paramInt2);
    l.c("octopus.CmdManager", localStringBuilder.toString());
    return m.a(SppProc.CMD_TYPE.SPP_GET_SD_MUSIC_LIST, new byte[] { i, j, k, m });
  }
  
  public static byte[] d(int paramInt, byte[] paramArrayOfByte)
  {
    org.greenrobot.eventbus.c.a().d(new com.divoom.Divoom.b.h.c());
    paramArrayOfByte = GlobalApplication.b().g().PixelEncodeSixteen(paramArrayOfByte, 1, 0);
    byte[] arrayOfByte = new byte[paramArrayOfByte.length + 1 + 1 + 2];
    arrayOfByte[0] = 0;
    arrayOfByte[1] = ((byte)(paramInt & 0xFF));
    arrayOfByte[2] = ((byte)(paramArrayOfByte.length & 0xFF));
    arrayOfByte[3] = ((byte)(paramArrayOfByte.length >> 8 & 0xFF));
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 4, paramArrayOfByte.length);
    return m.a(SppProc.CMD_TYPE.SPP_SAND_PAINT_CTRL, arrayOfByte);
  }
  
  public static byte[] d(boolean paramBoolean)
  {
    int j = 1;
    if (paramBoolean != true) {
      j = 0;
    }
    int i = (byte)j;
    return m.a(SppProc.CMD_TYPE.SPP_SET_HPUR_TYPE, new byte[] { i });
  }
  
  public static byte[] d(byte[] paramArrayOfByte)
  {
    return GlobalApplication.b().g().PixelEncodeSixteen(paramArrayOfByte, 1, 300);
  }
  
  public static byte[] e()
  {
    return m.a(SppProc.CMD_TYPE.SPP_GET_AUTO_POWER_OFF, null);
  }
  
  public static byte[] e(byte paramByte)
  {
    return m.a(SppProc.CMD_TYPE.SPP_SET_ALARM_LISTEN_VOLUME, new byte[] { paramByte });
  }
  
  public static byte[] e(int paramInt)
  {
    int i = (byte)paramInt;
    return m.a(SppProc.CMD_TYPE.SPP_SET_TOOL_INFO, new byte[] { 0, i });
  }
  
  public static byte[] e(int paramInt, byte[] paramArrayOfByte)
  {
    paramArrayOfByte = com.divoom.Divoom.led.b.a(paramArrayOfByte);
    if ((paramArrayOfByte != null) && (paramArrayOfByte.length != 0))
    {
      byte[] arrayOfByte = new byte[paramArrayOfByte.length + 4];
      arrayOfByte[0] = ((byte)(paramInt & 0xFF));
      arrayOfByte[1] = ((byte)(paramInt >> 8 & 0xFF));
      arrayOfByte[2] = ((byte)(paramArrayOfByte.length & 0xFF));
      arrayOfByte[3] = ((byte)(paramArrayOfByte.length >> 8 & 0xFF));
      System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 4, paramArrayOfByte.length);
      return m.a(SppProc.CMD_TYPE.SPP_DRAWING_ENCODE_MOVIE_PLAY, arrayOfByte);
    }
    return null;
  }
  
  public static byte[] e(boolean paramBoolean)
  {
    int j = 1;
    if (paramBoolean != true) {
      j = 0;
    }
    int i = (byte)j;
    return m.a(SppProc.CMD_TYPE.SPP_SET_SONG_DIS_CTRL, new byte[] { i });
  }
  
  public static byte[] e(byte[] paramArrayOfByte)
  {
    Object localObject = GlobalApplication.BlueModeEnum.getMode();
    GlobalApplication.BlueModeEnum localBlueModeEnum = GlobalApplication.BlueModeEnum.OldMode;
    int k = 0;
    if (localObject == localBlueModeEnum)
    {
      localObject = new byte['Ǵ'];
      localObject[0] = 0;
      localObject[1] = 10;
      localObject[2] = 10;
      localObject[3] = 4;
      int j = 0;
      int i = 4;
      while (j < 363)
      {
        if (j == 362) {
          localObject[i] = ((byte)(j(paramArrayOfByte[j]) >> 4));
        } else {
          localObject[i] = ((byte)(j(paramArrayOfByte[j]) >> 4 | j(paramArrayOfByte[(j + 1)]) >> 4 << 4));
        }
        i += 1;
        j += 2;
      }
      paramArrayOfByte = new byte[i];
      j = k;
      while (j < i)
      {
        paramArrayOfByte[j] = localObject[j];
        j += 1;
      }
      return m.a(SppProc.CMD_TYPE.SPP_SET_BOX_COLOR, paramArrayOfByte);
    }
    org.greenrobot.eventbus.c.a().d(new com.divoom.Divoom.b.h.c());
    paramArrayOfByte = GlobalApplication.b().g().PixelEncodeSixteen(paramArrayOfByte, 1, 0);
    localObject = new byte[paramArrayOfByte.length + 4];
    localObject[0] = 0;
    localObject[1] = 10;
    localObject[2] = 10;
    localObject[3] = 4;
    System.arraycopy(paramArrayOfByte, 0, localObject, 4, paramArrayOfByte.length);
    return m.a(SppProc.CMD_TYPE.SPP_SET_BOX_COLOR, (byte[])localObject);
  }
  
  public static byte[] f()
  {
    return m.a(SppProc.CMD_TYPE.SPP_GET_PLAY_VOICE_STATUS, null);
  }
  
  public static byte[] f(byte paramByte)
  {
    return m.a(SppProc.CMD_TYPE.SPP_SET_VOL, new byte[] { paramByte });
  }
  
  public static byte[] f(int paramInt)
  {
    org.greenrobot.eventbus.c.a().d(new com.divoom.Divoom.b.h.c());
    int i = (byte)paramInt;
    return m.a(SppProc.CMD_TYPE.SPP_GET_TOOL_INFO, new byte[] { i });
  }
  
  public static byte[] f(int paramInt, byte[] paramArrayOfByte)
  {
    int i = paramArrayOfByte.length;
    paramArrayOfByte = GlobalApplication.b().g().PixelEncodeSixteen(paramArrayOfByte, 1, 0);
    int j = paramArrayOfByte.length;
    byte[] arrayOfByte = new byte[j + 3];
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("通知收到图片");
    localStringBuilder.append(paramInt);
    LogUtil.e(localStringBuilder.toString());
    i = paramInt;
    if (paramInt >= 8) {
      i = paramInt + 1;
    }
    arrayOfByte[0] = ((byte)(i & 0xFF));
    arrayOfByte[1] = ((byte)(j & 0xFF));
    arrayOfByte[2] = ((byte)(j >> 8 & 0xFF));
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 3, paramArrayOfByte.length);
    return m.a(SppProc.CMD_TYPE.SPP_SET_ANCS_NOTICE_PIC, arrayOfByte);
  }
  
  public static byte[] f(boolean paramBoolean)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.copyTypes(TypeTransformer.java:311)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.fixTypes(TypeTransformer.java:226)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:207)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n");
  }
  
  public static byte[] f(byte[] paramArrayOfByte)
  {
    return m.a(SppProc.CMD_TYPE.SPP_LIGHT_ADJUST_LEVEL, paramArrayOfByte);
  }
  
  public static void g(int paramInt)
  {
    int i = (byte)paramInt;
    byte[] arrayOfByte = m.a(SppProc.CMD_TYPE.SPP_SET_NEW_MIX_MUSIC_MODE, new byte[] { i, -1 });
    k.a().b(arrayOfByte);
  }
  
  public static byte[] g()
  {
    return m.a(SppProc.CMD_TYPE.SPP_GET_SOUND_CTRL, null);
  }
  
  public static byte[] g(byte paramByte)
  {
    return m.a(SppProc.CMD_TYPE.SPP_GET_TIME_MANAGE_CTRL, new byte[] { paramByte });
  }
  
  public static byte[] g(boolean paramBoolean)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.copyTypes(TypeTransformer.java:311)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.fixTypes(TypeTransformer.java:226)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:207)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n");
  }
  
  public static byte[] g(byte[] paramArrayOfByte)
  {
    return m.a(SppProc.CMD_TYPE.SPP_SET_ANDROID_ANCS, paramArrayOfByte);
  }
  
  public static byte[] h()
  {
    return m.a(SppProc.CMD_TYPE.SPP_GET_ENERGY_CTRL, null);
  }
  
  public static byte[] h(byte paramByte)
  {
    return m.a(SppProc.CMD_TYPE.SPP_SET_MIX_MUISE_MODE, new byte[] { paramByte });
  }
  
  public static byte[] h(int paramInt)
  {
    int i = (byte)Color.red(paramInt);
    int j = (byte)Color.green(paramInt);
    int k = (byte)Color.blue(paramInt);
    return m.a(SppProc.CMD_TYPE.SPP_DRAWING_MUL_PAD_ENTER, new byte[] { i, j, k });
  }
  
  public static byte[] h(boolean paramBoolean)
  {
    org.greenrobot.eventbus.c.a().d(new com.divoom.Divoom.b.h.c());
    if (paramBoolean) {
      LogUtil.e("开启设备搜索");
    } else {
      LogUtil.e("关闭备搜索");
    }
    byte[] arrayOfByte;
    if (paramBoolean)
    {
      arrayOfByte = new byte[2];
      arrayOfByte[0] = ((byte)MultiControlEnum.startSearch._value);
      arrayOfByte[1] = 0;
    }
    else
    {
      arrayOfByte = new byte[1];
      arrayOfByte[0] = ((byte)MultiControlEnum.stopSearch._value);
    }
    return m.a(SppProc.CMD_TYPE.SPP_SET_MUL_DEVICE_CTRL, arrayOfByte);
  }
  
  public static byte[] h(byte[] paramArrayOfByte)
  {
    return m.a(SppProc.CMD_TYPE.SPP_APP_UPDATE_FILE_INFO, paramArrayOfByte);
  }
  
  public static byte[] i()
  {
    return m.a(SppProc.CMD_TYPE.SPP_DEL_LEAVE_MSG_GIF, null);
  }
  
  public static byte[] i(byte paramByte)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("----------->设置获取屏幕亮度  light");
    localStringBuilder.append(paramByte);
    com.divoom.Divoom.utils.d.a(localStringBuilder.toString());
    return m.a(SppProc.CMD_TYPE.SPP_SET_SYSTEM_BRIGHT, new byte[] { paramByte });
  }
  
  public static byte[] i(int paramInt)
  {
    int i = (byte)LightEnum.SPECIAL_MODE._value;
    int j = (byte)(paramInt & 0xFF);
    return m.a(SppProc.CMD_TYPE.SPP_SET_BOX_MODE, new byte[] { i, j });
  }
  
  public static byte[] i(boolean paramBoolean)
  {
    int i = (byte)paramBoolean;
    return m.a(SppProc.CMD_TYPE.SPP_DRAWING_CTRL_MOVIE_PLAY, new byte[] { i });
  }
  
  private static int j(byte paramByte)
  {
    return paramByte & 0xFF;
  }
  
  public static byte[] j()
  {
    return m.a(SppProc.CMD_TYPE.SPP_GET_BOX_MODE, null);
  }
  
  public static byte[] j(int paramInt)
  {
    byte[] arrayOfByte = new byte[10];
    arrayOfByte[0] = ((byte)LightEnum.MUSIC_MODE._value);
    arrayOfByte[1] = ((byte)(paramInt & 0xFF));
    return m.a(SppProc.CMD_TYPE.SPP_SET_BOX_MODE, arrayOfByte);
  }
  
  public static byte[] j(boolean paramBoolean)
  {
    int i = (byte)paramBoolean;
    return m.a(SppProc.CMD_TYPE.SPP_SET_PLAY_STATUS, new byte[] { i });
  }
  
  public static byte[] k()
  {
    return m.a(SppProc.CMD_TYPE.SPP_SEND_GAME_SHARK, null);
  }
  
  public static byte[] k(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("getNotifyTest ");
    localStringBuilder.append(paramInt);
    LogUtil.e(localStringBuilder.toString());
    int j = paramInt;
    if (paramInt >= 8) {
      j = paramInt + 1;
    }
    int i = (byte)(j & 0xFF);
    return m.a(SppProc.CMD_TYPE.SPP_SET_ANDROID_ANCS, new byte[] { i });
  }
  
  public static byte[] l()
  {
    return m.a(SppProc.CMD_TYPE.SPP_GET_SCENE, null);
  }
  
  public static byte[] l(int paramInt)
  {
    int i = (byte)(paramInt & 0xFF);
    int j = (byte)(paramInt >> 8 & 0xFF);
    return m.a(SppProc.CMD_TYPE.SPP_SET_SD_PLAY_MUSIC_ID, new byte[] { i, j });
  }
  
  public static byte[] m()
  {
    return m.a(SppProc.CMD_TYPE.SPP_SET_TEMP_TYPE, new byte[] { -1 });
  }
  
  public static byte[] m(int paramInt)
  {
    int i = (byte)paramInt;
    return m.a(SppProc.CMD_TYPE.SPP_SET_VOL, new byte[] { i });
  }
  
  public static byte[] n()
  {
    return m.a(SppProc.CMD_TYPE.SPP_SET_HPUR_TYPE, new byte[] { -1 });
  }
  
  public static byte[] n(int paramInt)
  {
    int i = (byte)(paramInt & 0xFF);
    int j = (byte)(paramInt >> 8 & 0xFF);
    return m.a(SppProc.CMD_TYPE.SPP_SET_SD_MUSIC_POSITION, new byte[] { i, j });
  }
  
  public static byte[] o()
  {
    return m.a(SppProc.CMD_TYPE.SPP_SET_SONG_DIS_CTRL, new byte[] { -1 });
  }
  
  public static byte[] o(int paramInt)
  {
    int i = (byte)(paramInt & 0xFF);
    return m.a(SppProc.CMD_TYPE.SPP_SET_SD_MUSIC_PLAY_MODE, new byte[] { i });
  }
  
  public static byte[] p()
  {
    return m.a(SppProc.CMD_TYPE.SPP_GET_DIALY_TIME_EXT2, null);
  }
  
  public static byte[] p(int paramInt)
  {
    int i = (byte)(paramInt & 0xFF);
    return m.a(SppProc.CMD_TYPE.SPP_MODIFY_RHYTHM_ITEMS, new byte[] { i });
  }
  
  public static byte[] q()
  {
    Object localObject = Calendar.getInstance();
    int i4 = ((Calendar)localObject).get(1);
    int i5 = i4 / 100;
    int i6 = ((Calendar)localObject).get(2);
    int i7 = ((Calendar)localObject).get(7) - 1;
    int i8 = ((Calendar)localObject).get(5);
    int i9 = ((Calendar)localObject).get(11);
    int i10 = ((Calendar)localObject).get(12);
    int i11 = ((Calendar)localObject).get(13);
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("year: ");
    ((StringBuilder)localObject).append(i4);
    ((StringBuilder)localObject).append("day: ");
    ((StringBuilder)localObject).append(i8);
    ((StringBuilder)localObject).append("hour : ");
    ((StringBuilder)localObject).append(i9);
    ((StringBuilder)localObject).append(" , minute : ");
    ((StringBuilder)localObject).append(i10);
    ((StringBuilder)localObject).append(" , second : ");
    ((StringBuilder)localObject).append(i11);
    ((StringBuilder)localObject).append("week: ");
    ((StringBuilder)localObject).append(i7);
    l.c("octopus.CmdManager", ((StringBuilder)localObject).toString());
    int i = (byte)(i4 % 100 & 0xFF);
    int j = (byte)(i5 & 0xFF);
    int k = (byte)(i6 + 1 & 0xFF);
    int m = (byte)(i8 & 0xFF);
    int n = (byte)(i9 & 0xFF);
    int i1 = (byte)(i10 & 0xFF);
    int i2 = (byte)(i11 & 0xFF);
    int i3 = (byte)(i7 & 0xFF);
    localObject = m.a(SppProc.CMD_TYPE.SPP_SET_SYSTEM_TIME, new byte[] { i, j, k, m, n, i1, i2, i3 });
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("发出的数据 : ");
    localStringBuilder.append(aw.a((byte[])localObject));
    l.c("octopus.CmdManager", localStringBuilder.toString());
    return (byte[])localObject;
  }
  
  public static byte[] q(int paramInt)
  {
    int i = (byte)(paramInt & 0xFF);
    return m.a(SppProc.CMD_TYPE.SPP_SET_SLEEP_CTRL_MODE, new byte[] { i });
  }
  
  public static byte[] r()
  {
    return m.a(SppProc.CMD_TYPE.SPP_GET_ALARM_TIME_SCENE, null);
  }
  
  public static byte[] s()
  {
    return m.a(SppProc.CMD_TYPE.SPP_GET_FM_CURRENT_FREQ, null);
  }
  
  public static byte[] t()
  {
    return m.a(SppProc.CMD_TYPE.SPP_SET_FM_AUTOMATIC_SEARCH, new byte[] { 0 });
  }
  
  public static byte[] u()
  {
    return m.a(SppProc.CMD_TYPE.SPP_GET_FM_COUNT_OR_FREQ, null);
  }
  
  public static byte[] v()
  {
    return m.a(SppProc.CMD_TYPE.SPP_GET_FM_REGION, null);
  }
  
  public static byte[] w()
  {
    return m.a(SppProc.CMD_TYPE.SPP_GET_STDB_MODE, null);
  }
  
  public static byte[] x()
  {
    return m.a(SppProc.CMD_TYPE.SPP_GET_PLAY_STATUS, null);
  }
  
  public static byte[] y()
  {
    return m.a(SppProc.CMD_TYPE.SPP_GET_VOL, null);
  }
  
  public static byte[] z()
  {
    return m.a(SppProc.CMD_TYPE.SPP_DRAWING_PAD_EXIT, null);
  }
  
  public static enum LightEnum
  {
    public int _value;
    
    static
    {
      HOT_MODE = new LightEnum("HOT_MODE", 2, 2);
      SPECIAL_MODE = new LightEnum("SPECIAL_MODE", 3, 3);
      MUSIC_MODE = new LightEnum("MUSIC_MODE", 4, 4);
      USER_DEFINE_MODE = new LightEnum("USER_DEFINE_MODE", 5, 5);
      WATCH_MODE = new LightEnum("WATCH_MODE", 6, 6);
    }
    
    private LightEnum(int paramInt)
    {
      this._value = paramInt;
    }
  }
  
  public static enum MultiControlEnum
  {
    int _value;
    
    static
    {
      startSearch = new MultiControlEnum("startSearch", 1, 1);
      exitMulti = new MultiControlEnum("exitMulti", 2, 2);
      exchangeMulti = new MultiControlEnum("exchangeMulti", 3, 3);
      setMultiMode = new MultiControlEnum("setMultiMode", 4, 4);
      getMultiInfo = new MultiControlEnum("getMultiInfo", 5, 5);
      deleteMulti = new MultiControlEnum("deleteMulti", 6, 6);
      refreshDevice = new MultiControlEnum("refreshDevice", 7, 7);
      showNumber = new MultiControlEnum("showNumber", 8, 8);
      mirrorOrfit = new MultiControlEnum("mirrorOrfit", 9, 10);
      getAllInfo = new MultiControlEnum("getAllInfo", 10, 17);
    }
    
    private MultiControlEnum(int paramInt)
    {
      this._value = paramInt;
    }
  }
  
  public static enum MultiModeEnum
  {
    int _value;
    
    private MultiModeEnum(int paramInt)
    {
      this._value = paramInt;
    }
  }
  
  public static enum MultiShowEnum
  {
    int _value;
    
    private MultiShowEnum(int paramInt)
    {
      this._value = paramInt;
    }
  }
}
