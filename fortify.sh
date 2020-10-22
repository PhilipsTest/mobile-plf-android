#!/bin/bash -l
set -e
chmod -R 755 .
./gradlew --refresh-dependencies		
array=(
    'registrationApi::UR_Android'
    'mec::MEC_ANDROID'
    'AppInfra::AppInfra_Android'
    'digitalCare::CC_Android'
    'philipsecommercesdk::ECS_ANDROID'
    'iap::IAP_Android'
    'pif::plf_android'
    'pim::PIM_Android'
    'conversationalChatBot::CCB_ANDROID'
    'product-registration-lib::PR_Android'
    'prx::PRX_Android'
    'securedblibrary::SecureDB_Android'
    'uAppFwLib::uAppFwLib_Android'
)
for index in "${array[@]}" ; do
    KEY="${index%%::*}"
    VALUE="${index##*::}"
  echo "*** sourceanalyzer -b $KEY -clean ***"
  sourceanalyzer -b $KEY -clean    
  echo "*** sourceanalyzer -b $KEY -Xmx10G -Xss32M -debug-verbose -logfile $VALUE.txt ./gradlew $KEY:assembleRelease ***"
  sourceanalyzer -b $KEY -Xmx10G -Xss32M -debug-verbose -logfile $VALUE.txt ./gradlew clean $KEY:assembleRelease
  echo "*** sourceanalyzer -b $KEY -scan -f $VALUE.fpr ***"
  sourceanalyzer -b $KEY -Xmx10G -Xss32M -scan -f $VALUE.fpr
  echo "*** fortifyclient -url https://fortify.philips.com/ssc $VALUE***"
  fortifyclient -url https://fortify.philips.com/ssc -authtoken 3a8cf873-d0b9-4c5a-9b87-4b7fc1978249 uploadFPR -file $VALUE.fpr -project EMS -version $VALUE
done
