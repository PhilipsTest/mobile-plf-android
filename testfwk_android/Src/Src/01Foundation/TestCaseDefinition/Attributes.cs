using System;
using System.Collections.Generic;
using System.Text;

namespace Philips.CDPAutomation.Foundation.TestCaseDefinition
{

    [AttributeUsage(AttributeTargets.Class | AttributeTargets.Method |
  AttributeTargets.Property, AllowMultiple=true)]
    public class TestcaseSetupAttribute : System.Attribute
    {

    }
    [AttributeUsage(AttributeTargets.Class | AttributeTargets.Method |
  AttributeTargets.Property, AllowMultiple=true)]
    public class TestcaseTeardownAttribute : System.Attribute
    {

    }
    [AttributeUsage(AttributeTargets.Class | AttributeTargets.Method |
  AttributeTargets.Property, AllowMultiple=true)]
    public class TestcaseExecuteAttribute : System.Attribute
    {

    }
    [AttributeUsage(AttributeTargets.Class | AttributeTargets.Method |
    AttributeTargets.Property, AllowMultiple=true)]
    public class Implementation : System.Attribute
    {
        String m_Implementation=String.Empty;
        public string Mode
        {
            get
            {
                return m_Implementation;
            }
            set
            {
                m_Implementation=value;
            }
        }

    }

    [AttributeUsage(AttributeTargets.Class | AttributeTargets.Method |
  AttributeTargets.Property, AllowMultiple=true)]
    public class TestcaseVerifyAttribute : System.Attribute
    {
        string m_type = String.Empty;
        public TestcaseVerifyAttribute(string name)
        {
            if (name != null)
            {
                m_type = name;
            }
        }
    }
    [AttributeUsage(AttributeTargets.Class | AttributeTargets.Method |
  AttributeTargets.Property, AllowMultiple=true)]
    public class ReUsableAttribute : System.Attribute
    {

    }

    [AttributeUsage(AttributeTargets.Class, AllowMultiple=true)]
    public class ProductAttribute : System.Attribute
    {
        ProductSpecifier m_product;
        String m_versionIntroduced;
        String m_versionRetired;
        public ProductSpecifier Product
        {
            get
            {
                return m_product;
            }
            set
            {
                m_product = value;
            }
        }
        public String VersionIntroduced
        {
            get
            {
                return m_versionIntroduced;
            }
            set
            {
                m_versionIntroduced = value;
            }
        }
        public String VersionRetired
        {
            get
            {
                return m_versionRetired;
            }
            set
            {
                m_versionRetired = value;
            }
        }

        private ProductAttribute()
        {
        }
        public ProductAttribute(ProductSpecifier product, string versionIntroduced, string versionRetired)
        {
            m_product = product;
            m_versionIntroduced = versionIntroduced;
            m_versionRetired = versionRetired;
        }

        public string IntroducedVersion
        {
            get
            {
                return m_versionIntroduced;
            }
        }
    }
    public class TestIdAttribute : Attribute
    {
        string m_testId;

        public TestIdAttribute(string testId)
        {
            m_testId = testId;
        }

        public string TestId
        {
            get
            {
                return m_testId;
            }
        }
    }

    public class ApplicationVersionAttribute : Attribute
    {
        string m_version;

        public ApplicationVersionAttribute(string version)
        {
            m_version = version;
        }

        public string MajorVersion
        {
            get
            {
                if (string.IsNullOrEmpty(m_version))
                {
                    return null;
                }
                else if (!m_version.Contains("."))
                {
                    throw new Exception("Invalid format supplied for version. No '.' exists");
                }

                string[] versions = m_version.Split(new char[] { '.' });
                return versions[0];
            }
        }

        public string MinorVersion
        {
            get
            {
                if (string.IsNullOrEmpty(m_version))
                {
                    return null;
                }
                else if (!m_version.Contains("."))
                {
                    throw new Exception("Invalid format supplied for version. No '.' exists");
                }

                string[] versions = m_version.Split(new char[] { '.' });
                return versions[1];
            }
        }

        public string Version
        {
            get
            {
                return m_version;
            }
        }
    }
    public class TestcasePropertiesAttribute : Attribute
    {
        string m_fids;
        ProductSpecifier m_product;
        string m_testNumberId;
        ProductModel[] m_supportedProductModels;
        SystemType[] m_supportedSystemTypes;

        public TestcasePropertiesAttribute (ProductSpecifier product, string fids, string testNumberId)
        {
            m_product = product;
            m_fids = fids;
            m_testNumberId = testNumberId;
        }

        public TestcasePropertiesAttribute (ProductSpecifier product,  string testNumberId, ProductModel[] supportedProductModels,
             SystemType[] systemTypes)
        {
            m_product = product;
            m_testNumberId = testNumberId;
            m_supportedProductModels = supportedProductModels;
            m_supportedSystemTypes = systemTypes;
        }

       
        public string TestNumberId
        {
            get
            {
                return m_testNumberId;
            }
        }

        public ProductSpecifier Product
        {
            get
            {
                return m_product;
            }
        }

        public ProductModel[] SupportedProductModels
        {
            get
            {
                return m_supportedProductModels;
            }
        }

       

        public SystemType[] SupportedSystemTypes
        {
            get
            {
                return m_supportedSystemTypes;
            }
        }
        
    }

    [AttributeUsage(AttributeTargets.Class, Inherited=false)]
    public class TestCaseInfoAttribute : System.Attribute
    {
        public TestCaseInfoAttribute()
        {
        }
        public TestCaseInfoAttribute(string name)
        {
            Name = name;
        }
        public string MainArea=Unspecified;
        public string SubArea=Unspecified;
        public string Implementation=Unspecified;
        public string Site=Unspecified;

        public string Name = Unspecified;
        public string Id = Unspecified;
        public string PreCondition = Unspecified;
        public string PostCondition = Unspecified;
        public string Description = Unspecified;
        public bool IsPartOfWorkflow = true;
        public bool IsWorkFlowDataDriven = false;
        public bool IsValidated = false;
        public bool IsDataDriven = false;
        const string Unspecified = "Not Specified";

        public override string ToString()
        {
            System.Text.StringBuilder stringBuilder = new System.Text.StringBuilder();
            stringBuilder.AppendFormat("\r\nName: {0}\r\n", Name);
            stringBuilder.AppendFormat("Id: {0}\r\n", Id);
            stringBuilder.AppendFormat("PreCondition: {0}\r\n", PreCondition);
            stringBuilder.AppendFormat("PostCondition: {0}\r\n", PostCondition);
            stringBuilder.AppendFormat("Description: {0}\r\n", Description);

            return stringBuilder.ToString();
        }
    }

    [AttributeUsage(AttributeTargets.Class | AttributeTargets.Method |
    AttributeTargets.Property, AllowMultiple=true)]
    public class RequirementAttribute : System.Attribute
    {
        private string m_req;

        public string REQ
        {
            get
            {
                return m_req;
            }
            set
            {
                m_req = value;
            }
        }

        public RequirementAttribute(string req)
        {
            m_req = req;
        }
    }

    [AttributeUsage(AttributeTargets.Class | AttributeTargets.Method | 
    AttributeTargets.Property, AllowMultiple=true)]
    public class TestcaseAttribute : System.Attribute
    {
        TestcaseState m_state = TestcaseState.UNKNOWN;
        TestcaseType m_type = TestcaseType.ACCEPTANCE;
        TestcaseSubType m_subType = TestcaseSubType.NONE;
        TestcaseError m_error = TestcaseError.NONE;
        string m_errorID = String.Empty;


        #region Constructors
        public TestcaseAttribute(TestcaseState tcState, TestcaseType tcType, TestcaseSubType tcSubType = TestcaseSubType.NONE)
        {
            m_state = tcState;
            m_type = tcType;
            m_subType = tcSubType;
        }

        public TestcaseAttribute(TestcaseState tcState, TestcaseType tcType, TestcaseError tcError, string BugNumber)
        {
            m_state = tcState;
            m_type = tcType;
            m_error = tcError;
            m_errorID = BugNumber;
        }

        #endregion

        #region Properties
        public TestcaseState State
        {
            get
            {
                return m_state;
            }
        }

        public TestcaseType Type
        {
            get
            {
                return m_type;
            }
        }

        public TestcaseError Error
        {
            get
            {
                return m_error;
            }
        }

        public String ErrorID
        {
            get
            {
                return m_errorID;
            }
        }
        public TestcaseSubType SubType
        {
            get
            {
                return m_subType;
            }
        }

        
        #endregion

    }

    [AttributeUsage(AttributeTargets.Class, Inherited=false)]
    public class VerificationTypeAttribute : System.Attribute
    {
        VerificationIndicators verificationType;
        public VerificationTypeAttribute()
        {
        }

        public VerificationTypeAttribute(VerificationIndicators name)
        {
            verificationType = name;
        }
        public string VerificationType
        {
            get
            {
                return verificationType.ToString();
            }
        }
    }
}

