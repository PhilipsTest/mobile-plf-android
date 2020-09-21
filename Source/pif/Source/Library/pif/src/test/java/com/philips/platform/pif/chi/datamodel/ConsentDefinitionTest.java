/*
 * Copyright (c) Koninklijke Philips N.V., 2018.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.pif.chi.datamodel;

import android.os.Parcel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 29)
public class ConsentDefinitionTest {

    private List<String> consentTypes;
    private ConsentDefinition consentDefinition;
    private ConsentDefinition expectedConsentDefinition;

    @Rule
    public final ExpectedException exception = ExpectedException.none();


    @Before
    public void Before()
    {
        consentTypes = Arrays.asList("consentA", "consentB");
        consentDefinition = new ConsentDefinition(1,2,consentTypes,99);
        expectedConsentDefinition = new ConsentDefinition(1,2,consentTypes,99);
    }

    @Test
    public void ConsentDefinitionConstructor() {
        assertEquals(1, consentDefinition.getText());
        assertEquals(2, consentDefinition.getHelpText());
        assertEquals( consentTypes, consentDefinition.getTypes());
        assertEquals(99, consentDefinition.getVersion());
        assertEquals(0, consentDefinition.getRevokeWarningText());

        assertEquals(false, consentDefinition.hasRevokeWarningText());
        assertEquals(-1057708992, consentDefinition.hashCode());
    }

    @Test
    public void ConsentDefinitionConstructorWithRevokeWarning() {
        consentDefinition = new ConsentDefinition(1, 2, consentTypes, 99, 8);

        assertEquals(1, consentDefinition.getText());
        assertEquals(2, consentDefinition.getHelpText());
        assertEquals(consentTypes, consentDefinition.getTypes());
        assertEquals(99, consentDefinition.getVersion());
        assertEquals(8, consentDefinition.getRevokeWarningText());

        assertEquals(true, consentDefinition.hasRevokeWarningText());
        assertEquals(-1057701304, consentDefinition.hashCode());
    }

    @Test
    public void ConsentDefinitionDescribeContents() {
        assertEquals(0, consentDefinition.describeContents());
    }

    @Test
    public void ConsentDefinitionTextManipulation() {
        consentDefinition.setText(100);
        assertEquals(100, consentDefinition.getText());

        assertNotEquals(expectedConsentDefinition, consentDefinition);
    }

    @Test
    public void ConsentDefinitionHelpTextManipulation() {
        consentDefinition.setHelpText(200);
        assertEquals(200, consentDefinition.getHelpText());

        assertEquals(false, expectedConsentDefinition.equals(consentDefinition));
    }

    @Test
    public void ConsentDefinitionConsentTypesManipulation() {

        List<String> newConsentTypes = Arrays.asList("consentX", "consentY", "consentZ");
        consentDefinition.setTypes(newConsentTypes);
        assertEquals(newConsentTypes, consentDefinition.getTypes());

        assertEquals(false, expectedConsentDefinition.equals(consentDefinition));
    }

    @Test
    public void ConsentDefinitionVersionManipulation() {
        consentDefinition.setVersion(999);
        assertEquals(999, consentDefinition.getVersion());

        assertEquals(false, expectedConsentDefinition.equals(consentDefinition));
    }

    @Test
    public void ConsentDefinitionRevokeTextmanipulation() {
        consentDefinition.setRevokeWarningText(800);
        assertEquals(800, consentDefinition.getRevokeWarningText());

        assertEquals(false, expectedConsentDefinition.equals(consentDefinition));
    }

    @Test
    public void ConsentDefinitionCheckEquals() {
        assertEquals(false, consentDefinition.equals( null));
        assertEquals(true, consentDefinition.equals( consentDefinition));
        assertEquals(true, expectedConsentDefinition.equals( consentDefinition));
        assertEquals(true, consentDefinition.equals( expectedConsentDefinition));
    }

    @Test
    public void ConsentDefinitionParcelableObject () {
        Parcel parcel = Parcel.obtain();

        consentDefinition.writeToParcel(parcel, consentDefinition.describeContents());
        parcel.setDataPosition(0);

        ConsentDefinition consentDefinitionFromParcel = ConsentDefinition.CREATOR.createFromParcel(parcel);

        assertEquals(expectedConsentDefinition, consentDefinitionFromParcel);

    }

    @Test
    public void ConsentDefinitionCreatorNewArray() {
        ConsentDefinition[] arrayOfConsentDefinition = ConsentDefinition.CREATOR.newArray(4);

        assertNotNull(arrayOfConsentDefinition);
        assertEquals(4, arrayOfConsentDefinition.length);
        assertArrayEquals(new ConsentDefinition[4], arrayOfConsentDefinition);
    }

    @Test
    public void ConsentDefinitionConstructorTypesListNull() {
        exception.expect(IllegalArgumentException.class);
        consentDefinition = new ConsentDefinition(1,2,null,99);
        //ends here
    }

    @Test
    public void ConsentDefinitionSetTypesListNull() {
        exception.expect(IllegalArgumentException.class);
        consentDefinition.setTypes(null);
        //ends here
    }

    @Test
    public void ConsentDefinitionConstructorEmptyTypesList() {
        consentTypes = Arrays.asList();

        exception.expect(IllegalArgumentException.class);
        consentDefinition = new ConsentDefinition(1,2,consentTypes,99);
        //ends here
    }

    @Test
    public void ConsentDefinitionSetTypesWithEmptyList() {
        consentTypes = Arrays.asList();

        exception.expect(IllegalArgumentException.class);
        consentDefinition.setTypes(consentTypes);
        //ends here
    }

}