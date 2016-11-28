/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.platform.uid.thememanager;

import android.content.res.Resources;

import com.philips.platform.uid.R;

public enum ColorRange {
    GROUP_BLUE {
        @Override
        public void injectColorRange(final Resources.Theme theme) {
            theme.applyStyle(R.style.GroupBlue, true);
        }
    },
    BLUE {
        @Override
        public void injectColorRange(final Resources.Theme theme) {
            theme.applyStyle(R.style.Blue, true);
        }
    },
    AQUA {
        @Override
        public void injectColorRange(final Resources.Theme theme) {
            theme.applyStyle(R.style.Aqua, true);
        }
    },
    GREEN {
        @Override
        public void injectColorRange(final Resources.Theme theme) {
            theme.applyStyle(R.style.Green, true);
        }
    },
    ORANGE {
        @Override
        public void injectColorRange(final Resources.Theme theme) {
            theme.applyStyle(R.style.Orange, true);
        }
    },
    PINK {
        @Override
        public void injectColorRange(final Resources.Theme theme) {
            theme.applyStyle(R.style.Pink, true);
        }
    },
    PURPLE {
        @Override
        public void injectColorRange(final Resources.Theme theme) {
            theme.applyStyle(R.style.Purple, true);
        }
    },
    GRAY {
        @Override
        public void injectColorRange(final Resources.Theme theme) {
            theme.applyStyle(R.style.Gray, true);
        }
    };

    public abstract void injectColorRange(Resources.Theme theme);

}
