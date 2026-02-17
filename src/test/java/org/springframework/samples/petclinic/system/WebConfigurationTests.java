package org.springframework.samples.petclinic.system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link WebConfiguration} to verify i18n configuration.
 *
 * <p>
 * These tests validate that the locale resolver and locale change interceptor are
 * properly configured to support language switching with session persistence.
 * </p>
 */
@DisplayName("WebConfiguration Tests")
class WebConfigurationTests {

	private WebConfiguration webConfiguration;

	@BeforeEach
	void setUp() {
		webConfiguration = new WebConfiguration();
	}

	@Test
	@DisplayName("should create LocaleResolver bean as SessionLocaleResolver")
	void shouldCreateLocaleResolverAsSessionLocaleResolver() {
		// Act
		LocaleResolver localeResolver = webConfiguration.localeResolver();

		// Assert
		assertThat(localeResolver).isNotNull();
		assertThat(localeResolver).isInstanceOf(SessionLocaleResolver.class);
	}

	@Test
	@DisplayName("should create LocaleChangeInterceptor bean with 'lang' parameter name")
	void shouldCreateLocaleChangeInterceptorWithLangParam() {
		// Act
		LocaleChangeInterceptor interceptor = webConfiguration.localeChangeInterceptor();

		// Assert
		assertThat(interceptor).isNotNull();
		assertThat(interceptor.getParamName()).isEqualTo("lang");
	}

	@Test
	@DisplayName("should register LocaleChangeInterceptor in interceptor registry")
	void shouldRegisterLocaleChangeInterceptor() {
		// Arrange
		InterceptorRegistry registry = mock(InterceptorRegistry.class);

		// Act
		webConfiguration.addInterceptors(registry);

		// Assert
		verify(registry, times(1)).addInterceptor(any(LocaleChangeInterceptor.class));
	}

	@Test
	@DisplayName("should use SessionLocaleResolver for session-based locale storage")
	void shouldUseSessionLocaleResolver() {
		// Act
		LocaleResolver localeResolver = webConfiguration.localeResolver();

		// Assert
		assertThat(localeResolver).isInstanceOf(SessionLocaleResolver.class);
	}

}
