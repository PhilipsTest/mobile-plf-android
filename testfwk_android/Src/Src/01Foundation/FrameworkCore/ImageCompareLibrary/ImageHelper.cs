using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;
using System.Drawing.Imaging;

namespace Philips.MRAutomation.Foundation.Common.ImageCompareLibrary
{
    public class ImageHelper
    {
        /// <summary>
        /// Convert the input image to a 24 bits-per-pixel image in case it has a different pixel depth.
        /// </summary>
        /// <param name="bmp">The bitmap to convert</param>
        /// <returns></returns>
        public static Bitmap ConvertImageTo24BitsPerPixel(Bitmap bmp)
        {
            if (bmp.PixelFormat != PixelFormat.Format24bppRgb)
            {
                Bitmap b24bits = new Bitmap(bmp.Width, bmp.Height, PixelFormat.Format24bppRgb);
                Graphics g = Graphics.FromImage(b24bits);
                g.DrawImage(bmp, 0, 0);
                g.Dispose();
                
                return b24bits;
            }
            else
            {
               
                return bmp; // Return the original image
            }
        }
    }
}

