using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;
using System.Drawing.Imaging;

namespace Philips.MRAutomation.Foundation.Common.ImageCompareLibrary
{
    public class ImageResizer
    {
        /// <summary>
        /// 
        /// </summary>
        /// <param name="bmp1"></param>
        /// <param name="bmp2"></param>
        public static void MatchImageSizes(ref Bitmap bmp1, ref Bitmap bmp2)
        {
            if (!bmp1.PhysicalDimension.Equals(bmp2.PhysicalDimension))
            {
                if (bmp1.Height < bmp2.Height)
                {
                    bmp1 = IncreaseHeight(bmp1, bmp2.Height - bmp1.Height);
                }
                else if (bmp1.Height > bmp2.Height)
                {
                    bmp2 = IncreaseHeight(bmp2, bmp1.Height - bmp2.Height);
                }

                if (bmp1.Width < bmp2.Width)
                {
                    bmp1 = IncreaseWidth(bmp1, bmp2.Width - bmp1.Width);
                }
                else if (bmp1.Width > bmp2.Width)
                {
                    bmp2 = IncreaseWidth(bmp2, bmp1.Width - bmp2.Width);
                }
            }
        }


        /// <summary>
        /// Crop an image file using a specified rectangle
        /// </summary>
        /// <param name="x">The X coordinate of the rectangle to crop</param>
        /// <param name="y">The Y coordinate of the rectangle to crop</param>
        /// <param name="width">The width of the rectangle to crop</param>
        /// <param name="height">The height of the rectangle to crop</param>
        /// <returns></returns>
        public static Bitmap CropImage(Bitmap bmp, int x, int y, int width, int height)
        {
            bmp = ImageHelper.ConvertImageTo24BitsPerPixel(bmp);
            Bitmap bmpCropped = new Bitmap(width, height, bmp.PixelFormat);
            bmpCropped.SetResolution(bmp.HorizontalResolution, bmp.VerticalResolution);

            Graphics g = Graphics.FromImage(bmpCropped);
            g.DrawImage(bmp, new Rectangle(0, 0, width, height), x, y, width, height, GraphicsUnit.Pixel);
            g.Dispose();

            return bmpCropped;
        }
        /// <summary>
        /// 
        /// </summary>
        /// <param name="bmp"></param>
        /// <param name="nrOfPixels"></param>
        /// <returns></returns>
        private static Bitmap IncreaseWidth(Bitmap bmp, int nrOfPixels)
        {
            // Lock the bits so a pointer to the first pixel can be obtained
            BitmapData bmpOriginalData = bmp.LockBits(new Rectangle(0, 0, bmp.Width, bmp.Height), ImageLockMode.ReadOnly, bmp.PixelFormat);

            // Create a new image that will hold the resized data
            Bitmap bmpResized = new Bitmap(bmp.Width + nrOfPixels, bmp.Height, bmp.PixelFormat);
            BitmapData bmpResizedData = bmpResized.LockBits(new Rectangle(0, 0, bmpResized.Width, bmpResized.Height), ImageLockMode.WriteOnly, bmpResized.PixelFormat);

            unsafe
            {
                int paddingOriginal = bmpOriginalData.Stride - bmpOriginalData.Width * 3;
                int paddingResized = bmpResizedData.Stride - bmpResizedData.Width * 3;

                byte* ptrOriginal = (byte*)bmpOriginalData.Scan0;
                byte* ptrResized = (byte*)bmpResizedData.Scan0;

                for (int i = 0; i < bmpOriginalData.Height; i++)
                {
                    for (int j = 0; j < bmpOriginalData.Width; j++)
                    {
                        ptrResized[0] = ptrOriginal[0];
                        ptrResized[1] = ptrOriginal[1];
                        ptrResized[2] = ptrOriginal[2];
                        ptrOriginal += 3;
                        ptrResized += 3;
                    }

                    // Add the additional pixels at the end of the row
                    for (int k = 0; k < nrOfPixels; k++)
                    {
                        // Add "magenta" pixels
                        ptrResized[0] = 255;
                        ptrResized[1] = 0;
                        ptrResized[2] = 255;
                        ptrResized += 3;
                    }

                    ptrOriginal += paddingOriginal;
                    ptrResized += paddingResized;
                }
            }

            bmp.UnlockBits(bmpOriginalData);
            bmp.Dispose();
            bmpResized.UnlockBits(bmpResizedData);
            return bmpResized;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="bmp"></param>
        /// <param name="nrOfPixels"></param>
        /// <param name="filename"></param>
        /// <returns></returns>
        private static Bitmap IncreaseHeight(Bitmap bmp, int nrOfPixels)
        {
            // Lock the bits so a pointer to the first pixel can be obtained
            BitmapData bmpOriginalData = bmp.LockBits(new Rectangle(0, 0, bmp.Width, bmp.Height), ImageLockMode.ReadOnly, bmp.PixelFormat);

            // Create a new image that will hold the resized data
            Bitmap bmpResized = new Bitmap(bmp.Width, bmp.Height + nrOfPixels, bmp.PixelFormat);
            BitmapData bmpResizedData = bmpResized.LockBits(new Rectangle(0, 0, bmpResized.Width, bmpResized.Height), ImageLockMode.WriteOnly, bmpResized.PixelFormat);

            unsafe
            {
                int paddingOriginal = bmpOriginalData.Stride - bmpOriginalData.Width * 3;
                int paddingResized = bmpResizedData.Stride - bmpResizedData.Width * 3;

                byte* ptrOriginal = (byte*)bmpOriginalData.Scan0;
                byte* ptrResized = (byte*)bmpResizedData.Scan0;

                for (int i = 0; i < bmpOriginalData.Height; i++)
                {
                    for (int j = 0; j < bmpOriginalData.Width; j++)
                    {
                        ptrResized[0] = ptrOriginal[0];
                        ptrResized[1] = ptrOriginal[1];
                        ptrResized[2] = ptrOriginal[2];
                        ptrOriginal += 3;
                        ptrResized += 3;
                    }
                    ptrOriginal += paddingOriginal;
                    ptrResized += paddingResized;
                }

                // Add the additional rows of pixels
                for (int k = 0; k < nrOfPixels; k++)
                {
                    for (int j = 0; j < bmpOriginalData.Width; j++)
                    {
                        // Add "magenta" pixels
                        ptrResized[0] = 255;
                        ptrResized[1] = 0;
                        ptrResized[2] = 255;
                        ptrResized += 3;
                    }
                    ptrOriginal += paddingOriginal;
                    ptrResized += paddingResized;
                }
            }

            bmp.UnlockBits(bmpOriginalData);
            bmp.Dispose();
            bmpResized.UnlockBits(bmpResizedData);

            return bmpResized;
        }
    }
}

