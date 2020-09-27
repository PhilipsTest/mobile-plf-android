# Contributing Guidelines

Thank you for your interest in contributing to our project. Whether it's a bug report, new feature, correction, or additional documentation, we greatly value feedback and contributions from our community.

Please read through this document before submitting any issues or pull requests to ensure we have all the necessary information to effectively respond to your contribution or bug report.

## Contributing via Pull Requests

_Note: ONLY applicable to [InnerSource REPO for EMS-Platform](https://tfsemea1.ta.philips.com/tfs/TPC_Region02/Innersource/_git/mobile-plf-android)

To raise a pull request please follow the below steps

1. Clone the repository and create a branch based off of master.
2. The Naming convention for the branch to be submitted should be <innersource/[bugfix/feature/spike]/[name]>.
    * e.g. `innersource/feature/fancy_animations` or `innersource/bugfix/crash_on_my_device`
3. Modify the source on the branch
    * We are strong believers in Clean Code, but please focus on the specific change you are contributing!If you also reformat code, e.g. apply the 'boyscout principle', it will be hard for us to focus on your change.
    * If you have any structural change, please find our suggestion: please create a separate branch, and describe the intention for your changes in the pull request.
4. Mandatory: Ensure that local build and all the required test cases passes.
5. After completing the work, Rebase and then push the changes to Forked repository ( git push origin <your branch>).
6. Create Pull request to [review and merge code]( https://docs.microsoft.com/en-us/vsts/git/pull-requests?view=vsts#from-the-code-view-on-the-web).
7. In addition to that, I would suggest dropping an mail to the reviewers.
8. Please note that the minimum number of reviewers is 2.
9. Mail will be sent to the requester post the changes from your branch are merged to master branch.
10.For any concern, please contact to IET Mobile Platform (Functional Account) <iet_mobileplatform@philips.com>

To send us a pull request, please:

1. Clone the repository and create a branch based off of master
2. The Naming convention for the branch to be submitted should be <code1_username_innersource>.
3. Modify the source on the branch ; please focus on the specific change you are contributing. If you also reformat all the code, it will be hard for us to focus on your change.
4. Ensure that local build and required test cases pass.
5. After completing the work, Rebase and then push the changes to repository ( git push origin <your branch>).
6. Create Pull request to review and merge code (Refer https://docs.microsoft.com/en-us/vsts/git/pull-requests?view=vsts#from-the-code-view-on-the-web if you need help to raise a pull request).
7. The default mandatory code Reviewers are rahul@philips.com , indrajit.kumar@philips.com and anurag.gautam@philips.com

