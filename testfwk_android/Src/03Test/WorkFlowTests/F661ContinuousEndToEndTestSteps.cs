﻿using Philips.H2H.Automation.Foundation.MobileTestCore;
using Philips.H2H.Foundation.AutomationCore;
using Philips.H2H.Foundation.AutomationCore.Mobile;
using Philips.SIG.Automation.Mobile.CDP.Repository;
using System;
using System.Threading;
using TechTalk.SpecFlow;
using System.Collections.Generic;
using Philips.H2H.Foundation.AutomationCore.Interface;
using System.Configuration;
using Philips.CDP.Automation.IAP.Tests.Workflows;
using Philips.SIG.Automation.Android.CDP.IAPTestPlugin;
using System.Xml;
using System.Reflection;
using System.IO;
using Philips.SIG.Automation.Android.CDPP.AppFramework_TestPlugin;
using NUnit.Framework;
namespace Philips.CDP.Automation.IAP.Tests.Workflows
{

    [Binding]
    public class F661ContinuousEndToEndTestSteps
    {



        [Then(@"I am on end to end smoke test screen")]
        public void ThenIClickOnConnectivityFromTheHamburgerMenuList()
        {
            AppHomeScreen.Click(AppHomeScreen.Button.Connectivity);
            Thread.Sleep(2000);
        }


        [When(@"I connect to the BLE reference device and retrieve battery level")]

        public void ThenIClickOnNucleousValue()
        {
          // AppHomeScreen.Click(AppHomeScreen.Button.NucleousDevice);
           // Thread.Sleep(10000);
              AppHomeScreen.EnterText(AppHomeScreen.EditText.Nucleus, "30");
            Thread.Sleep(2000);
            string battery_nucleus = AppHomeScreen.GetText(AppHomeScreen.EditText.Nucleus).Trim();
            Logger.Info("Nucleous battery info: " + battery_nucleus);
            IapReport.Message("Nucleous battery info: " + battery_nucleus);
        }

        [Then(@"I verify the nucleous value on screen shows '(.*)'")]
        public void ThenIVerifyTheNucleousValueOnScreenShows(int p0)
        {
            string nucleous_value = AppHomeScreen.GetText(AppHomeScreen.EditText.Nucleus).Trim();
            Assert.AreEqual(
                p0, Convert.ToInt32(nucleous_value),
                "Failed: Battery values fetched from nucleous is incorrect - " +
                "Expected: " + p0 + ", but Nucleous value fetched is: " + nucleous_value
            );
        }

        [When(@"I get the latest moment from datacore")]
        public void ThenIClickOnMomentValueFromDatacore()
        {
            AppHomeScreen.Click(AppHomeScreen.Button.MomentValueFromDatacore);
            Thread.Sleep(5000);
            string battery_nucleus = AppHomeScreen.GetText(AppHomeScreen.EditText.Nucleus).Trim();
            string moment_value = AppHomeScreen.GetText(AppHomeScreen.EditText.Moment).Trim();
            Logger.Info("Moment battery info: " + moment_value);

            if (string.IsNullOrEmpty(moment_value))
            {
                Assert.Fail(AppHomeScreen.GetDatacoreErrorMsg());
            }

            if (moment_value.Equals(battery_nucleus))
            {
                Logger.Info("Passed: battery values are the same: " + battery_nucleus);
                IapReport.Message("Passed: battery values are the same: " + battery_nucleus);
            }
            else
            {
                Logger.Info("Failed: battery values are not the same: " + battery_nucleus + " : " + moment_value);
                IapReport.Message("Failed: battery values are not the same: " + battery_nucleus + " : " + moment_value);
            }
        }

        [Then(@"I verify the moment on screen shows '(.*)'")]
        public void ThenIVerifyTheMomentOnScreenShows(int p0)
        {
            string battery_nucleus = AppHomeScreen.GetText(AppHomeScreen.EditText.Nucleus).Trim();
            string moment_value = AppHomeScreen.GetText(AppHomeScreen.EditText.Moment).Trim();
            Assert.AreEqual(
                p0, Convert.ToInt32(moment_value),
                "Failed: Battery values are not the same - " +
                "Nucleous value is: " + battery_nucleus + ", Moment value is : " + moment_value
            );
        }


    }
}
