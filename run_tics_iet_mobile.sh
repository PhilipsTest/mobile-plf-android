#!/bin/bash

if mount | grep "on /mnt/tics" > /dev/null; then
    echo "TICS Share is mounted"
else
    echo "TICS Share is not mounted"
    echo "Mount TICS Share"
    /mnt/connect_tics_share.sh
fi
export TICS=/mnt/tics/configurations/IET-EMS
export PATH=/mnt/tics/wrappers/macos/Client:${PATH}
export PATH=${PATH}:/mnt/tics/wrappers/macos/BuildServer

echo "Running TICS..."
TICSMaintenance -project EMS_Android -branchname develop -branchdir .
TICSQServer -project EMS_Android -calc ALL -recalc COMPILERWARNING -nosanity -tmpdir ./EMS_Android