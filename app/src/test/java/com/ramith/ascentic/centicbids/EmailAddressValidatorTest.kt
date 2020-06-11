package com.ramith.ascentic.centicbids

import com.ramith.ascentic.centicbids.utils.EmailAddressValidator
import org.junit.Test
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue


class EmailAddressValidatorTest {

    private val emailValidator = EmailAddressValidator()

    @Test
    fun emailValidator_EmptyString_ReturnsFalse() {
        assertFalse(emailValidator.isValidEmail(""))
    }

    @Test
    fun emailValidator_NullEmail_ReturnsFalse() {
        assertFalse(emailValidator.isValidEmail(null))
    }

    @Test
    fun emailValidator_CorrectEmailSimple_ReturnsTrue() {
        assertTrue(emailValidator.isValidEmail("user@email.com"))
    }

    @Test
    fun emailValidator_CorrectEmailSubDomain_ReturnsTrue() {
        assertTrue(emailValidator.isValidEmail("user@email.com.eu"))
    }

    @Test
    fun emailValidator_InvalidEmailNoDomain_ReturnsFalse() {
        assertFalse(emailValidator.isValidEmail("user@email"))
    }

    @Test
    fun emailValidator_InvalidEmailDoubleDots_ReturnsFalse() {
        assertFalse(emailValidator.isValidEmail("user@email..com"))
    }

    @Test
    fun emailValidator_InvalidEmailNoUsername_ReturnsFalse() {
        assertFalse(emailValidator.isValidEmail("@email.com"))
    }


}