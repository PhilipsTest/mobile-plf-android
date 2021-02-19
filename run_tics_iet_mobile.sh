#!/bin/bash

if mount | grep "on /Volumes/tics" > /dev/null; then
    echo "TICS Share is mounted"
else
    echo "TICS Share is not mounted"
    echo "Mount TICS Share"
    /mnt/connect_tics_share.sh
fi
export TICS=/Volumes/tics/configurations/IET-EMS
export PATH=/Volumes/tics/wrappers/macos/Client:${PATH}
export PATH=${PATH}:/Volumes/tics/wrappers/macos/BuildServer

echo "Running TICS..."
TICSMaintenance -project EMS_Android -branchname develop -branchdir .
TICSQServer -project EMS_Android -calc ALL -recalc COMPILERWARNING -nosanity -tmpdir ./EMS_Android
