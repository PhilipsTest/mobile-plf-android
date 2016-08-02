using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;
using System.Drawing.Imaging;

namespace Philips.MRAutomation.Foundation.Common.ImageCompareLibrary
{
    /// <summary>
    ///  This Comparer uses Pearsons correlation coefficient algorithm implemented in C# by Bochkanov Sergey Copyright 09.04.2007 
    /// (http://www.alglib.net/statistics/correlation.php)
    /// 
    /// What is a correlation? (Pearson correlation)
    /// A correlation is a number between -1 and +1 that measures the degree of association between two variables (call them X and Y). 
    /// A positive value for the correlation implies a positive association (large values of X tend to be associated with large values of Y and small values of X tend to be associated with small values of Y). 
    /// A negative value for the correlation implies a negative or inverse association (large values of X tend to be associated with small values of Y and vice versa).
    /// 
    /// @References: 
    /// http://en.wikipedia.org/wiki/Pearson_product-moment_correlation_coefficient
    /// </summary>
    public class ImageComparer
    {
        private Bitmap image1;

        public Bitmap Image1
        {
            get { return image1; }
        }

        private Bitmap image2;

        public Bitmap Image2
        {
            get { return image2; }
        }

        public double PearsonCorrelation
        {
            get
            {
                byte[] bmp1Pixels = ConvertPixelDataToByteArray(image1);
                byte[] bmp2Pixels = ConvertPixelDataToByteArray(image2);
                return PearsonCorrelationCalculation(ref bmp1Pixels, ref bmp2Pixels, bmp1Pixels.Length);
            }
        }
        /// <summary>
        /// Constructor of ImageComparer.
        /// Takes two Bitmap objects. Internally they are converted to 24bpp
        /// </summary>
        /// <param name="image1">The first image</param>
        /// <param name="image2">The second image</param>
        public ImageComparer(Bitmap image1, Bitmap image2)
        {
            this.image1 = ImageHelper.ConvertImageTo24BitsPerPixel(image1);
            this.image2 = ImageHelper.ConvertImageTo24BitsPerPixel(image2);
        }

        /// <summary>
        /// Convert the pixel data of a 24bpp image to a byte array.
        /// </summary>
        /// <param name="bmp">BitMap object (24 bits)</param>
        /// <returns>A byte[] containing the pixel data of the specified bitmap</returns>
        private byte[] ConvertPixelDataToByteArray(Bitmap bmp)
        {
            // Lock the bits so a pointer to the first pixel can be obtained
            BitmapData bmpData = bmp.LockBits(new Rectangle(0, 0, bmp.Width, bmp.Height), ImageLockMode.ReadOnly, bmp.PixelFormat);

            // Declare an array to hold the bytes of the image
            int bytes = bmp.Width * bmp.Height * 3; // 24 bits-per-pixel = 3 bytes-per-pixel
            byte[] rgbValues = new byte[bytes];

            // "Stride" is the width, in bytes, of a single row of pixel data in the locked array. 
            // This width must be multiple of 4 for efficiency reasons and may therefor be padded.
            int widthInBytes = bmpData.Width * 3;
            int padding = bmpData.Stride - widthInBytes;

            unsafe
            {
                byte* ptr = (byte*)bmpData.Scan0;

                for (int i = 0; i < bmpData.Height; i++)
                {
                    for (int j = 0; j < bmpData.Width; j++)
                    {
                        // Store the R, G, and B values.
                        rgbValues[(i * widthInBytes + j * 3) + 0] = ptr[0];
                        rgbValues[(i * widthInBytes + j * 3) + 1] = ptr[1];
                        rgbValues[(i * widthInBytes + j * 3) + 2] = ptr[2];
                        ptr += 3;
                    }
                    ptr += padding;
                }
            }

            // Unlock the bits again
            bmp.UnlockBits(bmpData);

            // Return the byte[]
            return rgbValues;
        }

        /// <summary>
        /// Determine the difference between each pixel in two images and return it as a new image.
        /// Equal pixels are black.
        /// </summary>
        public Bitmap DifferenceAsImage
        {
            get
            {
                if (image1.PhysicalDimension.Equals(image2.PhysicalDimension))
                {
                    // Lock the bits so a pointer to the first pixel in memory can be obtained
                    BitmapData bmpData1 = image1.LockBits(new Rectangle(0, 0, image1.Width, image1.Height), ImageLockMode.ReadOnly, image1.PixelFormat);
                    BitmapData bmpData2 = image2.LockBits(new Rectangle(0, 0, image2.Width, image2.Height), ImageLockMode.ReadOnly, image2.PixelFormat);

                    // Create a new image that will hold the difference data
                    Bitmap bmpDiff = new Bitmap(image1.Width, image1.Height, image1.PixelFormat);
                    BitmapData bmpDiffData = bmpDiff.LockBits(new Rectangle(0, 0, bmpDiff.Width, bmpDiff.Height), ImageLockMode.WriteOnly, bmpDiff.PixelFormat);

                    int stride = bmpData1.Stride;
                    IntPtr Scan01 = bmpData1.Scan0;
                    IntPtr Scan02 = bmpData2.Scan0;
                    IntPtr ScanDiff = bmpDiffData.Scan0;

                    unsafe
                    {
                        byte* pBmp1 = (byte*)(void*)Scan01;   // Get a pointer to the first pixel of bmp1
                        byte* pBmp2 = (byte*)(void*)Scan02;   // Get a pointer to the first pixel of bmp2
                        byte* pDiff = (byte*)(void*)ScanDiff; // Get a pointer to the first pixel of bmpDiff

                        int nOffset = stride - image1.Width * 3; // 24 bits-per-pixel = 3 bytes-per-pixel
                        int nWidth = image1.Width * 3;

                        for (int y = 0; y < image1.Height; ++y)
                        {
                            for (int x = 0; x < nWidth; ++x)
                            {
                                pDiff[0] = (byte)Math.Abs(pBmp1[0] - pBmp2[0]);
                                ++pBmp1;
                                ++pBmp2;
                                ++pDiff;
                            }
                            pBmp1 += nOffset;
                            pBmp2 += nOffset;
                            pDiff += nOffset;
                        }
                    }

                    image1.UnlockBits(bmpData1);
                    image2.UnlockBits(bmpData2);
                    bmpDiff.UnlockBits(bmpDiffData);

                    return bmpDiff;
                }
                else
                {
                    throw new Exception("* ERROR: Could not create a \"difference\" image since both input images were not equal in size");
                }
            }
        }

        /// <summary>
        /// Function to check the thresold value of the Diffrence images.
        /// If Threshold is less than user provided thrshold, return true else return false.
        /// </summary>
        /// <param name="firstImage">Bitmap Image</param>
        /// <param name="secondImage">Bitmap Image</param>
        /// <param name="thresholdValue">Thresold value in double(data type)</param>
        /// <returns>bool</returns>
        public static bool CheckImageDiffThreshold
            (Bitmap firstImage, Bitmap secondImage, string patientName, double thresholdValue)
        {
            bool flag = false;
            int sourceImgPixcel_Count = 0;
            int diffPixcel_Count = 0;
            for (int i = 0; i < firstImage.Width; i++)
            {
                for (int j = 0; j < firstImage.Height; j++)
                {
                    if (firstImage.GetPixel(i, j) != secondImage.GetPixel(i, j))
                    {
                        diffPixcel_Count++;
                    }
                    sourceImgPixcel_Count++;
                }
            }
            double percetage = ((double)diffPixcel_Count / (double)sourceImgPixcel_Count) * 100;
            if (percetage >= thresholdValue)
            {
                flag = false;
            }
            else
            {
                flag = true;
            }
            return flag;
        }

        # region Correlation Algorithm

        /// <summary>
        /// Pearson product-moment correlation coefficient
        /// Copyright 09.04.2007 by Bochkanov Sergey
        /// http://www.alglib.net/statistics/correlation.php
        /// </summary>
        /// <param name="x">sample 1 (array indexes: [0..N-1])</param>
        /// <param name="y">sample 2 (array indexes: [0..N-1])</param>
        /// <param name="n">sample size</param>
        /// <returns>Pearson product-moment correlation coefficient</returns>
        public static double PearsonCorrelationCalculation(ref byte[] x, ref byte[] y, int n)
        {
            double result = 0;
            int i = 0;
            double xmean = 0;
            double ymean = 0;
            double s = 0;
            double xv = 0;
            double yv = 0;
            double t1 = 0;
            double t2 = 0;

            xv = 0;
            yv = 0;
            if (n <= 1)
            {
                result = 0;
                return result;
            }

            //
            // Mean
            //
            xmean = 0;
            ymean = 0;
            for (i = 0; i <= n - 1; i++)
            {
                xmean = xmean + Convert.ToDouble(x[i]);
                ymean = ymean + Convert.ToDouble(y[i]);
            }
            xmean = xmean / n;
            ymean = ymean / n;

            //
            // numerator and denominator
            //
            s = 0;
            xv = 0;
            yv = 0;
            for (i = 0; i <= n - 1; i++)
            {
                t1 = x[i] - xmean;
                t2 = y[i] - ymean;
                xv = xv + (t1 * t1);
                yv = yv + (t2 * t2);
                s = s + t1 * t2;
            }
            if (xv == 0 | yv == 0)
            {
                result = 0;
            }
            else
            {
                result = s / (System.Math.Sqrt(xv) * System.Math.Sqrt(yv));
            }

            return result;
        }
        #endregion Algorithm

    }
}

