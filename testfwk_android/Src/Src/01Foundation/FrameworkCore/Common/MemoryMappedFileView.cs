

using System;
using System.Runtime.InteropServices;
using System.Runtime.Serialization.Formatters.Binary;
using System.IO; 

namespace Philips.MRAutomation.Foundation.FrameworkCore.Common
{
	public class MemoryMappedFileView : IDisposable
	{
		private IntPtr mappedView;
		private readonly int size;
		private readonly ViewAccess access;

		public enum ViewAccess : int
		{
			ReadWrite = 2,
			Read = 4,
		}

		internal MemoryMappedFileView(IntPtr mappedView, int size, ViewAccess access)
		{
			this.mappedView = mappedView;
			this.size = size;
			this.access = access;
		}

		public int Size
		{
			get {return size;}
		}

		public void ReadBytes(byte[] data)
		{
			ReadBytes(data,0);
		}
		public void ReadBytes(byte[] data, int offset)
		{
			for(int i=0;i<data.Length;i++)
				data[i] = Marshal.ReadByte(mappedView,offset+i);
		}

		public void WriteBytes(byte[] data)
		{
			WriteBytes(data,0);
		}
		public void WriteBytes(byte[] data, int offset)
		{
			for(int i=0;i<data.Length;i++)
				Marshal.WriteByte(mappedView,offset+i,data[i]);
		}

		#region Additional Accessors
		public byte ReadByte(int offset)
		{
			return Marshal.ReadByte(mappedView,offset);
		}
		public void WriteByte(byte data, int offset)
		{
			Marshal.WriteByte(mappedView,offset,data);
		}

		public short ReadInt16(int offset)
		{
			return Marshal.ReadInt16(mappedView,offset);
		}
		public void WriteInt16(short data, int offset)
		{
			Marshal.WriteInt16(mappedView,offset,data);
		}

		public int ReadInt32(int offset)
		{
			return Marshal.ReadInt32(mappedView,offset);
		}
		public void WriteInt32(int data, int offset)
		{
			Marshal.WriteInt32(mappedView,offset,data);
		}

		public long ReadInt64(int offset)
		{
			return Marshal.ReadInt64(mappedView,offset);
		}
		public void WriteInt64(long data, int offset)
		{
			Marshal.WriteInt64(mappedView,offset,data);
		}

		public object ReadStructure(Type structureType)
		{
			return Marshal.PtrToStructure(mappedView,structureType);
		}
		public void WriteStructure(object data)
		{
			Marshal.StructureToPtr(data,mappedView,true);
		}

		public object ReadDeserialize()
		{
			return ReadDeserialize(0,size);
		}
		public object ReadDeserialize(int offset)
		{
			return ReadDeserialize(offset,size-offset);
		}
		public object ReadDeserialize(int offset, int length)
		{
			byte[] binaryData = new byte[length];
			ReadBytes(binaryData,offset);
			BinaryFormatter formatter = new BinaryFormatter();
			MemoryStream ms = new MemoryStream(binaryData,0,length,true,true);
			object data = formatter.Deserialize(ms);
			ms.Close();
			return data;
		}

		/// <summary>
		/// Serializes the data and writes it to the file.
		/// </summary>
		/// <param name="data">The data to serialize.</param>
		public void WriteSerialize(object data)
		{
			WriteSerialize(data,0,size);
		}
		/// <summary>
		/// Serializes the data and writes it to the file.
		/// </summary>
		/// <param name="data">The data to serialize.</param>
		/// <param name="offset">The position in the file to start.</param>
		public void WriteSerialize(object data, int offset)
		{
			WriteSerialize(data,0,size-offset);
		}
		/// <summary>
		/// Serializes the data and writes it to the file.
		/// </summary>
		/// <param name="data">The data to serialize.</param>
		/// <param name="offset">The position in the file to start.</param>
		/// <param name="length">The buffer size in bytes.</param>
		public void WriteSerialize(object data, int offset, int length)
		{
			BinaryFormatter formatter
				= new BinaryFormatter();
			byte[] binaryData = new byte[length];
			MemoryStream ms = new MemoryStream(binaryData,0,length,true,true);
			formatter.Serialize(ms,data);
			ms.Flush();
			ms.Close();
			WriteBytes(binaryData,offset);
		}
		#endregion

		#region IDisposable Member

		public void Dispose()
		{
			if(mappedView != IntPtr.Zero)
			{
				if(NTKernel.UnmapViewOfFile(mappedView))
					mappedView = IntPtr.Zero;
			}
		}

		#endregion
	}
}

