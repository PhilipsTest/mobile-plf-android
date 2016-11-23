using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;
using System.Drawing.Imaging;

namespace Philips.MRAutomation.Foundation.Common.ImageCompareLibrary
{
    public class ImageHistogram
    {
        private Bitmap image;

        private int[] histogram = new int[256];

        public int[] Histogram
        {
            get { return histogram; }
        }

        private bool filterPureBlackAndWhite;

        public bool FilterPureBlackAndWhite
        {
            get { return filterPureBlackAndWhite; }
            set
            {
                filterPureBlackAndWhite = value;
                if (filterPureBlackAndWhite)
                {
                    histogram[0] = 0;
                    histogram[255] = 0;
                }
                else
                {
                    CreateHistogram();
                }
            }
        }

        /// <summary>
        /// The number of non black pixels. Note that this may be influenced by
        /// the <see cref="FilterPureBlackAndWhite"/>.
        /// </summary>
        public int NumberOfNonBlackPixels
        {
            get
            {
                int amount = 0;

                // Starting with i = 1 skips the pure black pixels located in histogram[0].
                for (int i = 1; i < histogram.Length; i++)
                {
                    amount += histogram[i];
                }

                return amount;
            }
        }
           
        public Bitmap Image
        {
            get { return image; }
        }

        public ImageHistogram(Bitmap image)
        {
            this.image = ImageHelper.ConvertImageTo24BitsPerPixel(image);
            CreateHistogram();
        }

        public Bitmap HistogramAsImage
        {
            get
            {
                return GetHistogramAsImage();
            }
        }

        #region image handling functions

        /// <summary>
        /// 
        /// </summary>
        /// <param name="bmp"></param>
        private void CreateHistogram()
        {
            BitmapData bmpData = image.LockBits(new System.Drawing.Rectangle(0, 0, image.Width, image.Height), ImageLockMode.ReadWrite, PixelFormat.Format24bppRgb);

            unsafe
            {
                // "Stride" is the width, in bytes, of a single row of pixel data in the locked array. 
                // This width must be multiple of 4 for efficiency reasons and may therefor be padded.
                int padding = bmpData.Stride - bmpData.Width * 3;

                byte* ptr = (byte*)bmpData.Scan0;

                // Set all entries to the value 0.
                for (int i = 0; i < histogram.Length; i++)
                {
                    histogram[i] = 0;
                }

                for (int i = 0; i < bmpData.Height; i++)
                {
                    for (int j = 0; j < bmpData.Width; j++)
                    {
                        // Get the average of the R, G, and B values.
                        int mean = ptr[0] + ptr[1] + ptr[2];
                        mean /= 3;

                        histogram[mean]++;
                        ptr += 3;
                    }
                    ptr += padding;
                }
            }

            image.UnlockBits(bmpData);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="filename"></param>
        private Bitmap GetHistogramAsImage()
        {
            int IMG_HEIGHT = 256;

            Bitmap bmp = new Bitmap(histogram.Length, IMG_HEIGHT, PixelFormat.Format24bppRgb);
            BitmapData bmpData = bmp.LockBits(new System.Drawing.Rectangle(0, 0, bmp.Width, bmp.Height), ImageLockMode.ReadWrite, PixelFormat.Format24bppRgb);

            

            // Find largest amount of pixels with the same color.
            // This determines the highest peak of the histogram.
            int max = 0;
            for (int i = 0; i < histogram.Length; i++)
            {
                if (max < histogram[i])
                {
                    max = histogram[i];
                }
            }

            unsafe
            {
                // "Stride" is the width, in bytes, of a single row of pixel data in the locked array. 
                // This width must be multiple of 4 for efficiency reasons and may therefor be padded.
                int padding = bmpData.Stride - bmpData.Width * 3;

                // Get a pointer to the first byte of the image.
                byte* ptr = (byte*)bmpData.Scan0;

                // Set the background color for all pixels in the bitmap
                for (int i = 0; i < bmpData.Height; i++)
                {
                    for (int j = 0; j < bmpData.Width; j++)
                    {
                        // Paint the pixel light gray.
                        ptr[0] = ptr[1] = ptr[2] = 232;
                        // Move the pointer one pixel forward.
                        ptr += 3;
                    }
                    // Take the optional padding at the end of the row into account.
                    ptr += padding;
                }

                // Draw a column for each histogram entry only when there is something to draw.
                if (max > 0)
                {
                    for (int i = 0; i < histogram.Length; i++)
                    {
                        // Get a pointer to the first byte of the image.
                        ptr = (byte*)bmpData.Scan0;

                        // Set the pointer to the base of the column.
                        ptr += (bmpData.Stride * (IMG_HEIGHT - 1)) + (i * 3);

                        // Determine the height of the column.
                        int columnHeight = (int)histogram[i] * IMG_HEIGHT / max;
                        for (int j = 0; j < columnHeight; j++)
                        {
                            // Paint the pixel black.
                            ptr[0] = ptr[1] = ptr[2] = 0;
                            // Move the pointer one row up in the same column.
                            ptr -= bmpData.Stride;
                        }
                    }
                }
            }

            bmp.UnlockBits(bmpData);
            return bmp;
        }

        #endregion
    }
}

