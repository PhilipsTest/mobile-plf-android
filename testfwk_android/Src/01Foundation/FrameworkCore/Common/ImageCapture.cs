using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;
using System.IO;
using System.Windows.Forms;
using System.Reflection;
using System.Drawing.Imaging;


namespace Philips.MRAutomation.Foundation.FrameworkCore.Common
{
    public class ImageCapture
    {
        public static Bitmap CaptureBitmap (IntPtr ptr)
        {
            IntPtr hDesktopDC = NativeAPI.GetDC(ptr);
            IntPtr hCaptureDC = NativeAPI.CreateCompatibleDC(hDesktopDC);

            Size sz = NativeAPI.GetSize(ptr);
            IntPtr hCaptureBitmap = NativeAPI.CreateCompatibleBitmap(hDesktopDC,
                                    Convert.ToInt32(sz.Width),
                                    Convert.ToInt32(sz.Height));
            NativeAPI.SelectObject(hCaptureDC, hCaptureBitmap);
            NativeAPI.BitBlt(hCaptureDC, 0, 0,
                             Convert.ToInt32(sz.Width),
                             Convert.ToInt32(sz.Height),
                             hDesktopDC, 0, 0, NativeAPI.SRCCOPY | NativeAPI.CAPTUREBLT);
            return Bitmap.FromHbitmap(hCaptureBitmap);
        }

        public static bool PixelCompare (string goldenbitmap, Bitmap AUTbmp)
        {
            bool flag = true;
            Bitmap img1 = new Bitmap(goldenbitmap);
            Bitmap img2 = AUTbmp;
            if (img1.Width == img2.Width && img1.Height == img2.Height)
            {
                for (int i = 0; i < img1.Width; i++)
                {
                    for (int j = 0; j < img1.Height; j++)
                    {
                        Color img1_ref = img1.GetPixel(i, j);
                        Color img2_ref = img2.GetPixel(i, j);
                        if (img1_ref != img2_ref)
                        {
                            flag = false;
                            Console.WriteLine("PixelCompare failed");
                            break;
                        }
                    }
                    if (flag == false)
                    {
                        break;
                    }
                }
            }
            else
            {
                Console.WriteLine("PixelCompare width and height are not same");
                flag = false;
            }
            return flag;
        }
        public static bool PixelCompare (Bitmap goldenbitmap, Bitmap AUTbmp)
        {
            bool flag = true;
            Bitmap img1 = goldenbitmap;
            Bitmap img2 = AUTbmp;
            if (img1.Width == img2.Width && img1.Height == img2.Height)
            {
                for (int i = 0; i < img1.Width; i++)
                {
                    for (int j = 0; j < img1.Height; j++)
                    {
                        Color img1_ref = img1.GetPixel(i, j);
                        Color img2_ref = img2.GetPixel(i, j);
                        if (img1_ref != img2_ref)
                        {
                            flag = false;
                            Console.WriteLine("PixelCompare failed");
                            break;
                        }
                    }
                    if (flag == false)
                    {
                        break;
                    }
                }
            }
            else
            {
                Console.WriteLine("PixelCompare width and height are not same");
                flag = false;
            }
            return flag;
        }


        public static bool Capture_CompareBitmap (Bitmap captureBitmap, string goldbitmap, string message)
        {
            bool bResult = true;
            string bitmapPath = GeneralConfiguration.GraphicalVerification.BitmapCapturePath;
            string diffbitmapPath = GeneralConfiguration.GraphicalVerification.DiffBitmapPath;
            if (GeneralConfiguration.GraphicalVerification.isBitmapCapture.ToLower() == "true")
            {
                captureBitmap.Save(Path.Combine(bitmapPath, goldbitmap));
            }

            Bitmap diffBitmap = null;
            Bitmap goldenBitmap = new Bitmap(Path.Combine(bitmapPath, goldbitmap));
            Philips.MRAutomation.Foundation.Common.ImageCompareLibrary.ImageComparer imgComparer =
                new Philips.MRAutomation.Foundation.Common.ImageCompareLibrary.ImageComparer(captureBitmap, goldenBitmap);
            try
            {
                if (!PixelCompare(goldenBitmap, captureBitmap))
                    diffBitmap = imgComparer.DifferenceAsImage;
            }
            catch (Exception)
            {
                bResult = true;
            }

            if (diffBitmap != null)
            {
                diffBitmap.Save(Path.Combine(diffbitmapPath, "Diff_" + goldbitmap));
                bResult = false;
            }
            string.Format(@"Bitmap Failure: {0}", message);
            return bResult;
        }
        public static bool Store_CompareBitmap (Bitmap captureBitmap, string goldbitmap, out Bitmap diffBitmap, out string diffBitmapPath)
        {
            bool bResult = true;
            diffBitmap = null;
            diffBitmapPath = string.Empty;
            string bitmapPath = GeneralConfiguration.GraphicalVerification.BitmapCapturePath;
            string diffbitmapPath = GeneralConfiguration.GraphicalVerification.DiffBitmapPath;
            if (GeneralConfiguration.GraphicalVerification.isBitmapCapture.ToLower() == "true")
            {
                captureBitmap.Save(Path.Combine(bitmapPath, goldbitmap));
            }
            Bitmap goldenBitmap = new Bitmap(Path.Combine(bitmapPath, goldbitmap));
            Philips.MRAutomation.Foundation.Common.ImageCompareLibrary.ImageComparer imgComparer =
                new Philips.MRAutomation.Foundation.Common.ImageCompareLibrary.ImageComparer(captureBitmap, goldenBitmap);
            try
            {
                if (!PixelCompare(goldenBitmap, captureBitmap))
                    diffBitmap = imgComparer.DifferenceAsImage;
            }
            catch (Exception)
            {
                bResult = true;
            }

            if (diffBitmap != null)
            {
                diffBitmapPath = Path.Combine(diffbitmapPath, "Diff_" + goldbitmap);
                diffBitmap.Save(diffBitmapPath);
                bResult = false;
            }
            return bResult;
        }

        /// <summary>
        /// Capture the screen and saves it to reports folder.
        /// </summary>
        /// <param name="filename">File name to save the screen capture</param>
        public static void CaptureScreen(string filename, ImageFormat imgFrmt=null)
        {
            imgFrmt = imgFrmt == null ? ImageFormat.Png : imgFrmt;
            string dir = Path.GetDirectoryName(Assembly.GetExecutingAssembly().GetModules()[0].FullyQualifiedName) +
                @"\Reports";
            CaptureScreen(filename, imgFrmt, dir);
        }

        /// <summary>
        /// Capture the current screen in the specified image format and saves it to the specifed location.
        /// </summary>
        /// <param name="Name of the file to save as (w/o extension)"></param>
        /// <param name="Image format of the file. (Default: Bmp)"></param>
        /// <param name="Location to store the image. (Default: Reports folder)"></param>
        public static void CaptureScreen(string filename, ImageFormat imgFormat, string imageStorePath = "")
        {
            Bitmap Bitmap;
            Graphics Graps;
            if (imageStorePath == "")
                imageStorePath = Path.GetDirectoryName(Assembly.GetExecutingAssembly().GetModules()[0].FullyQualifiedName) +
                @"\Reports";
            Bitmap = new Bitmap(System.Windows.Forms.Screen.PrimaryScreen.Bounds.Width,
                System.Windows.Forms.Screen.PrimaryScreen.Bounds.Height,
                PixelFormat.Format32bppArgb);
            Graps = Graphics.FromImage(Bitmap);
            Graps.CopyFromScreen(System.Windows.Forms.Screen.PrimaryScreen.Bounds.X,
                System.Windows.Forms.Screen.PrimaryScreen.Bounds.Y,
                0,
                0,
                System.Windows.Forms.Screen.PrimaryScreen.Bounds.Size,
                CopyPixelOperation.SourceCopy);

            if (!Directory.Exists(imageStorePath))
            {
                Directory.CreateDirectory(imageStorePath);
            }
            if (!File.Exists(imageStorePath + "\\" + filename + "." + imgFormat.ToString()))
            {
                Bitmap.Save(imageStorePath + "\\" + filename + "." + imgFormat.ToString(), imgFormat);
            }
            else
            {
                Bitmap.Save(imageStorePath + "\\" + filename + "_" + DateTime.Now.Ticks.ToString() + "." + imgFormat.ToString(), imgFormat);
            }
        }
    }
}

