using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;
using System.Drawing.Imaging;

namespace Philips.MRAutomation.Foundation.Common.ImageCompareLibrary
{
    public class ImageMasker
    {
        public static void MaskImage(ref Bitmap mask, ref Bitmap imgToBeMasked)
        {
            // Lock the bits so a pointer to the first pixel in memory can be obtained
            BitmapData bmpData1 = mask.LockBits(new Rectangle(0, 0, mask.Width, mask.Height), ImageLockMode.ReadOnly, mask.PixelFormat);
            BitmapData bmpData2 = imgToBeMasked.LockBits(new Rectangle(0, 0, imgToBeMasked.Width, imgToBeMasked.Height), ImageLockMode.ReadOnly, imgToBeMasked.PixelFormat);

            int stride = bmpData1.Stride;
            IntPtr Scan01 = bmpData1.Scan0;
            IntPtr Scan02 = bmpData2.Scan0;

            unsafe
            {
                byte* pBmp1 = (byte*)(void*)Scan01;   // Get a pointer to the first pixel of bmp1
                byte* pBmp2 = (byte*)(void*)Scan02;   // Get a pointer to the first pixel of bmp2

                int nOffset = stride - mask.Width * 3; // 24 bits-per-pixel = 3 bytes-per-pixel
                int nWidth = mask.Width * 3;

                for (int y = 0; y < mask.Height; ++y)
                {
                    for (int x = 0; x < nWidth; ++x)
                    {
                        // Paint the pixel black when in the mask it is black
                        if (pBmp1[0] + pBmp1[1] + pBmp1[2] == 0)
                        {
                            pBmp2[0] = pBmp2[1] = pBmp2[2] = 0;
                        }
                        ++pBmp1;
                        ++pBmp2;
                    }
                    pBmp1 += nOffset;
                    pBmp2 += nOffset;
                }
            }

            mask.UnlockBits(bmpData1);
            imgToBeMasked.UnlockBits(bmpData2);
        }
    }
}

