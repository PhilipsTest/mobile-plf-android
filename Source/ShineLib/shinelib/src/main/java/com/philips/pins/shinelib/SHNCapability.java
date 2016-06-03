/*
 * Copyright (c) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */

package com.philips.pins.shinelib;

/**
 * A placeholder interface. Provides better type safety over using Object. {@code SHNCapability} is reported to the user of API via {@link com.philips.pins.shinelib.SHNDevice#getCapabilityForType(SHNCapabilityType)}.
 * Returned instance is not type strong. The API user needs to caste it to the proper class type.
 */
public interface SHNCapability {
}
