using System;
using System.Collections.Generic;
using System.Text;

namespace Philips.CDPAutomation.Foundation.TestCaseDefinition
{

    public enum RunnerState
    {
        NOT_STARTED,
        RUNNING,
        STOPPED,
        STOP_PENDING,
        STOP_EXCEPTION,
        RESTARTING
    };
    public enum TestResult
    {
        FAIL,
        PASS,
        BLOCKED,
        INCOMPLETE,
        NOT_RUN
    };
    public enum VerificationIndicators
    {
        BITMAP,
        LOGIC,
	    MANUAL
    };

    public enum ProductSpecifier
    {
       IAP
    };
    public enum TestNotImplementedReason
    {
        MISSING_FUNCTIONALITY,
        MISSING_DOCUMENTATION,
        OUT_OF_SCOPE
    };

    public enum TestcaseState
    {
        UNKNOWN,
        GOOD,
        ERROR,
        RETIRED,
        UNTESTABLE
    };
    public enum TestcaseError
    {
        NONE,
        AMBIGUOUS,
        OVERLOADED,
        UNTESTABLE,
        OTHER
    };
    public enum TestcaseType
    {
       
        FUNCTIONALITY,
        PERFORMANCE,
        STRESS,
        RELIABILITY,
        ACCEPTANCE,
        
    };
    public enum TestcaseSubType
    {
        SMOKETEST,
        FUNCTIONALITY,
        FOCUS,
        PERFORMANCE,
        STRESS,
        RELIABILITY,
        ACCEPTANCE,
        NONE,
        IAP_REGRESSION
    };
    public enum TestcaseComplexity
    {
        HIGH,
        MEDIUM,
        LOW
    };

    

    public enum ProductModel
    {
        None
    };

    

    public enum SystemType
    {
        MOBILE
    };
}

