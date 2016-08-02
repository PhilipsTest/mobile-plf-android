#region System
using System;
using System.IO;
using System.Runtime.Serialization;
using System.Collections.Generic;
using System.Drawing;
using System.Runtime.Serialization.Formatters.Binary;
#endregion System


namespace Philips.MRAutomation.Foundation.FrameworkCore.Common
{
    /// <summary>
    /// Class to do a file transfer; to circumvent the object size restriction.
    /// </summary>
    public class BinarySerializer
    {
        #region Fields
        /// <summary>
        /// To transfer the Point contents into file.
        /// </summary>
        private Stream stream = null;
        private IFormatter formatter;
        private string serializedStream="C:\\serializer.bin";
        private string filename = String.Empty;
        #endregion Fields

        #region Constructor
        public BinarySerializer()
        {
            formatter = new BinaryFormatter();
        }
        #endregion Constructor

        #region Public Methods
        public bool Initialize(string id, bool serialize)
        {
            filename = String.Format("C:\\Temp\\{0}.bin", id);
            if (serialize)
            {
                stream = new FileStream(filename, FileMode.Create, FileAccess.Write, FileShare.None);
            }
            else
            {
                stream = new FileStream(filename, FileMode.Open, FileAccess.Read, FileShare.Read);
            }

            return stream != null;
        }
        /// <summary>
        /// Writes the ROI contents to a temporary file.
        /// This will be read by the calling code
        /// </summary>
        public void Serialize(IList<Point> points)
        {
            formatter.Serialize(stream, points);
        }
        /// <summary>
        /// Writes the Table contents to a temporary file.
        /// This will be read by the calling code
        /// </summary>
        public void Serialize(Dictionary<String, Dictionary<String, String>> table)
        {
            formatter.Serialize(stream, table);
        }
        /// <summary>
        /// Reads the contents from the temporary file.
        /// </summary>
        public IList<Point> GetPoints()
        {
            return formatter.Deserialize(stream) as IList<Point>;
        }
        /// <summary>
        /// Reads the contents from the temporary file.
        /// </summary>
        public Dictionary<String, Dictionary<String, String>> GetTable()
        {
            return formatter.Deserialize(stream) as Dictionary<String, Dictionary<String, String>>;
        }
        /// <summary>
        /// Releases the stream.
        /// </summary>
        public void Close()
        {
            stream.Close();
            Cleanup();
        }
        /// <summary>
        /// Deletes the temporary file.
        /// </summary>
        public void Cleanup()
        {
            File.Delete(filename);
        }
        /// <summary>
        /// This Function Serializes the Object into a File Stream
        /// for retrieving the object, at a later point in time.
        /// </summary>
        public void Serialize(object dataObject)
        {
            FileStream fileStream = File.Create(serializedStream);
            BinaryFormatter binaryFormatter = new BinaryFormatter();
            binaryFormatter.Serialize(fileStream, dataObject);
            fileStream.Close();
        }
        /// <summary>
        /// This Deserializes the object from the file Stream
        /// and gives back the object .
        /// </summary>
        /// <returns></returns>
        public object DeSerialize()
        {
            FileStream fileStream = File.Open(serializedStream, FileMode.Open);
            BinaryFormatter binaryFormatter = new BinaryFormatter();
            object retrievedObject = binaryFormatter.Deserialize(fileStream);
            fileStream.Close();
            return retrievedObject;
        }
        #endregion Public Methods
    }
}
#region Revision History
/// 22-Nov-2009 Rossel
///             To support ROI co-ordinate transfer.
/// 01-Dec-2009 Rossel
///             Added Serialization code for Dictionary of Dictionaries; 
/// 01-Dec-2009 Pattabhi
///             reviewed: Change the Initialize function to 
///             Point to executing assembly path; 

#endregion
