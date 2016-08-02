using System;

namespace Philips.CDPAutomation.Foundation.TestCaseDefinition
{
  public interface ITestCase
  {
    object Setup(params object[] parmaters);
    object ExecuteAction(params object[] parmaters);
    object VerifyAction(params object[] parameters);
    object Teardown(params object[] parmaters);
    object ReusableFunction(params object[] parmaters);
   
  }

    public class TestCase : ITestCase
    {

        #region ITestCase Members

        public virtual object Setup(params object[] parmaters)
        {
            throw new Exception("The method or operation is not implemented.");
        }

        public virtual object ExecuteAction(params object[] parmaters)
        {
            throw new Exception("The method or operation is not implemented.");
        }

        public virtual object VerifyAction(params object[] parameters)
        {
            throw new Exception("The method or operation is not implemented.");
        }

        public virtual object Teardown(params object[] parmaters)
        {
            throw new Exception("The method or operation is not implemented.");
        }

        public virtual object ReusableFunction(params object[] parmaters)
        {
            throw new Exception("The method or operation is not implemented.");
        }

        #endregion
    }

}

