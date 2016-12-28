/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lanou.lilyxiao.myapplication.util;

import android.media.ExifInterface;
import android.util.Log;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class Exif {
    private static final String TAG = "CameraExif";

    private static final int STATUS_OK = 0;
    private static final int STATUS_ERROR_NO_EXIF = 1;
    private static final int STATUS_ERROR_NO_ORI_TAG = 2;
    private static final int STATUS_INVALID_LENGTH = 3;
    private static final int STATUS_INVALID_BYTE_ORDER = 4;

    public static int getOrientation(byte[] jpeg) {
        if (jpeg == null) {
            return 0;
        }

        int offset = 0;
        int length = 0;

        // ISO/IEC 10918-1:1993(E)
        while (offset + 3 < jpeg.length && (jpeg[offset++] & 0xFF) == 0xFF) {
            int marker = jpeg[offset] & 0xFF;

            // Check if the marker is a padding.
            if (marker == 0xFF) {
                continue;
            }
            offset++;

            // Check if the marker is SOI or TEM.
            if (marker == 0xD8 || marker == 0x01) {
                continue;
            }
            // Check if the marker is EOI or SOS.
            if (marker == 0xD9 || marker == 0xDA) {
                break;
            }

            // Get the length and check if it is reasonable.
            length = pack(jpeg, offset, 2, false);
            if (length < 2 || offset + length > jpeg.length) {
                Log.e(TAG, "Invalid length");
                return 0;
            }

            // Break if the marker is EXIF in APP1.
            if (marker == 0xE1 && length >= 8 && pack(jpeg, offset + 2, 4, false) == 0x45786966
                    && pack(jpeg, offset + 6, 2, false) == 0) {
                offset += 8;
                length -= 8;
                break;
            }

            // Skip other markers.
            offset += length;
            length = 0;
        }

        // JEITA CP-3451 Exif Version 2.2
        if (length > 8) {
            // Identify the byte order.

            int tag = pack(jpeg, offset, 4, false);
            if (tag != 0x49492A00 && tag != 0x4D4D002A) {
                Log.e(TAG, "Invalid byte order");
                return 0;
            }
            boolean littleEndian = (tag == 0x49492A00);

            // Get the offset and check if it is reasonable.
            int count = pack(jpeg, offset + 4, 4, littleEndian) + 2;
            if (count < 10 || count > length) {
                Log.e(TAG, "Invalid offset");
                return 0;
            }
            offset += count;
            length -= count;

            // Get the count and go through all the elements.
            count = pack(jpeg, offset - 2, 2, littleEndian);
            while (count-- > 0 && length >= 12) {
                // Get the tag and check if it is orientation.
                tag = pack(jpeg, offset, 2, littleEndian);
                if (tag == 0x0112) {
                    // We do not really care about type and count, do we?
                    int orientation = pack(jpeg, offset + 8, 2, littleEndian);
                    switch (orientation) {
                        case 1:
                            return 0;
                        case 3:
                            return 180;
                        case 6:
                            return 90;
                        case 8:
                            return 270;
                    }
                    Log.i(TAG, "Unsupported orientation");
                    return 0;
                }
                offset += 12;
                length -= 12;
            }
        }

        Log.i(TAG, "Orientation not found");
        return 0;
    }

    static class OffsetInfo {
        int offset;
        int offsetValue;
    }

    public static byte[] setOrientation(byte[] jpegData, int degree) {
        ByteBuffer buffer = ByteBuffer.allocate(jpegData.length + 20);
        int status = -1;
        if (jpegData == null) {
            return null;
        }

        int offset = 0;
        int length = 0;

        // ISO/IEC 10918-1:1993(E)
        while (offset + 3 < jpegData.length && (jpegData[offset++] & 0xFF) == 0xFF) {
            buffer.put((byte) 0xFF);
            int marker = jpegData[offset] & 0xFF;
            buffer.put(jpegData[offset]);
            // Check if the marker is a padding.
            if (marker == 0xFF) {
                continue;
            }
            offset++;

            // Check if the marker is SOI or TEM.
            if (marker == 0xD8 || marker == 0x01) {
                continue;
            }
            // Check if the marker is EOI or SOS.
            if (marker == 0xD9 || marker == 0xDA) {
                status = STATUS_ERROR_NO_EXIF;
                break;
            }

            // Get the length and check if it is reasonable.
            length = pack(jpegData, offset, 2, false);
            if (length < 2 || offset + length > jpegData.length) {
                status = STATUS_INVALID_LENGTH;
                Log.e(TAG, "Invalid length");
                return null;
            }

            // Break if the marker is EXIF in APP1.
            if (marker == 0xE1 && length >= 8 && pack(jpegData, offset + 2, 4, false) == 0x45786966
                    && pack(jpegData, offset + 6, 2, false) == 0) {
                buffer.put(jpegData, offset, 8);
                offset += 8;
                length -= 8;
                break;
            }

            // Skip other markers.
            buffer.put(jpegData, offset, length);
            offset += length;
            length = 0;
        }

        if (status == STATUS_ERROR_NO_EXIF) {

        } else {
            if (length > 8) {
                // Identify the byte order.
                ArrayList<OffsetInfo> offsetList = new ArrayList<OffsetInfo>();
                int tag = pack(jpegData, offset, 4, false);
                if (tag != 0x49492A00 && tag != 0x4D4D002A) {
                    Log.e(TAG, "Invalid byte order");
                    return null;
                }

                buffer.put(jpegData, offset, 4);
                boolean littleEndian = (tag == 0x49492A00);
                buffer.order(littleEndian ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);

                // Get the offset and check if it is reasonable.
                int count = pack(jpegData, offset + 4, 4, littleEndian) + 2;
                if (count < 10 || count > length) {
                    Log.e(TAG, "Invalid offset");
                    return null;
                }

                buffer.put(jpegData, offset + 4, 4);
                offset += count;
                length -= count;

                int exifStart = buffer.position();
                int exifOffset = -1;
                int offsetValue = -1;
                buffer.put(jpegData, offset - 2, 2);
                // Get the count and go through all the elements.
                count = pack(jpegData, offset - 2, 2, littleEndian);
                int totalCount = count;
                while (count-- > 0 && length >= 12) {
                    // Get the tag and check if it is orientation.
                    tag = pack(jpegData, offset, 2, littleEndian);
                    buffer.put(jpegData, offset, 2);
                    if (tag == 0x0112) {
                        // We do not really care about type and count, do we?
                        int originalDegree = 0;
                        short orientation = (short) pack(jpegData, offset + 8, 2, littleEndian);
                        switch (orientation) {
                            case 1:
                                originalDegree = 0;
                                break;
                            case 3:
                                originalDegree = 180;
                                break;
                            case 6:
                                originalDegree = 90;
                                break;
                            case 8:
                                originalDegree = 270;
                                break;
                        }

                        originalDegree += degree;
                        originalDegree /= 360;

                        switch (originalDegree) {
                            case 0:
                                orientation = 1;
                                break;
                            case 180:
                                orientation = 3;
                                break;
                            case 90:
                                orientation = 5;
                                break;
                            case 270:
                                orientation = 8;
                                break;
                        }

                        buffer.put(jpegData, offset + 2, 6);
                        buffer.putShort(orientation);

                        buffer.put(jpegData, offset + 10, 2);
                        status = STATUS_OK;
                        offset += 12;
                        break;
                    }

                    if (tag == 0x8769 || tag == 0x010f || tag == 0x0110) {
                        OffsetInfo info = new OffsetInfo();
                        info.offset = buffer.position() + 6;
                        info.offsetValue = pack(jpegData, offset + 8, 4, littleEndian);
                        offsetList.add(info);
                    }

                    buffer.put(jpegData, offset + 2, 10);
                    offset += 12;
                    length -= 12;
                }

                if (status == STATUS_OK) {
                    buffer.put(jpegData, offset, jpegData.length - offset);
                    return buffer.array();
                } else {
                    buffer.putShort((short) 0x0112);
                    buffer.putShort((short) 0x03);
                    buffer.putInt(0x01);

                    short originalDegree = 0;
                    switch (degree) {
                        case 0:
                            originalDegree = 1;
                            break;
                        case 180:
                            originalDegree = 3;
                            break;
                        case 90:
                            originalDegree = 5;
                            break;
                        case 270:
                            originalDegree = 8;
                            break;
                    }

                    buffer.putShort(originalDegree);
                    buffer.putShort((short) 0);

                    int nPos = buffer.position();
                    for (OffsetInfo info : offsetList) {
                        buffer.position(info.offset);
                        buffer.putInt(info.offsetValue + 12);
                        buffer.position(nPos);
                    }

                    buffer.position(exifStart);
                    buffer.putShort((short) (++totalCount));
                    buffer.position(nPos);

                    int nextIDF = pack(jpegData, offset, 4, littleEndian);
                    buffer.putInt(nextIDF + 12);
                    offset -= 4;
                    buffer.put(jpegData, offset, jpegData.length - offset);
                    return buffer.array();
                }
            }
        }

        return null;
    }

    public static void setOri(String filePath, int ori) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filePath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Log.v(TAG, exif.getAttribute(ExifInterface.TAG_ISO));
        Log.v(TAG, exif.getAttribute(ExifInterface.TAG_GPS_ALTITUDE));
        Log.v(TAG, exif.getAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF));
    }

    private static String getExifOrientation(int orientation) {
        switch (orientation) {
            case 0:
                return String.valueOf(ExifInterface.ORIENTATION_NORMAL);
            case 90:
                return String.valueOf(ExifInterface.ORIENTATION_ROTATE_90);
            case 180:
                return String.valueOf(ExifInterface.ORIENTATION_ROTATE_180);
            case 270:
                return String.valueOf(ExifInterface.ORIENTATION_ROTATE_270);
            default:
                throw new AssertionError("invalid: " + orientation);
        }
    }

    private static int pack(byte[] bytes, int offset, int length, boolean littleEndian) {
        int step = 1;
        if (littleEndian) {
            offset += length - 1;
            step = -1;
        }

        int value = 0;
        while (length-- > 0) {
            value = (value << 8) | (bytes[offset] & 0xFF);
            offset += step;
        }
        return value;
    }

    public static byte[] writeFilter(byte[] data, String filterName) {
//        JpegImageMetadata jpegMetadata;
//        ByteArrayOutputStream outputstream = null;
//        try {
//            jpegMetadata = (JpegImageMetadata) Sanselan.getMetadata(data);
//
//            TiffOutputSet outputSet = null;
//            if (jpegMetadata != null) {
//                TiffImageMetadata exif = jpegMetadata.getExif();
//                outputSet = exif.getOutputSet();
//            }
//            if (outputSet != null) {
//                List<TiffOutputDirectory> dirs = outputSet.getDirectories();
//                for (TiffOutputDirectory dir : dirs) {
//                    if (dir.type != TiffOutputDirectory.DIRECTORY_TYPE_ROOT
//                            && dir.type != TiffOutputDirectory.DIRECTORY_TYPE_SUB)
//                        continue;
//                    TiffOutputField dateTimeField = dir.findField(ExifTagConstants.EXIF_TAG_MODEL);
//                    if (dateTimeField != null) {
//                        dir.removeField(ExifTagConstants.EXIF_TAG_MODEL);
//                    }
//                    TiffOutputField rotateField = TiffOutputField.create(
//                            TiffConstants.EXIF_TAG_MODEL, outputSet.byteOrder,new String(filterName.getBytes("GBK"),"GBK")  );
//                    dir.add(rotateField);
//                }
//            } else {
//                outputSet = new TiffOutputSet();
//                TiffOutputDirectory dir = outputSet.getOrCreateExifDirectory();
//                TiffOutputField rotateField = TiffOutputField.create(TiffConstants.EXIF_TAG_MODEL,
//                        outputSet.byteOrder, new String(filterName.getBytes("GBK"),"GBK") );
//                dir.add(rotateField);
//            }
//            outputstream = new ByteArrayOutputStream();
//            new ExifRewriter().updateExifMetadataLossless(data, outputstream, outputSet);
//            return outputstream.toByteArray();
//
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        finally {
//        	Util.closeSilently(outputstream);
//        }
        return data;
    }
    
}
