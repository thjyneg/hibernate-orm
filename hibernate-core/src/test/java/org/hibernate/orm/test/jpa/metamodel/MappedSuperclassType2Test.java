/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.orm.test.jpa.metamodel;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.ManagedType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.jpa.boot.spi.Bootstrap;
import org.hibernate.testing.orm.jpa.PersistenceUnitDescriptorAdapter;

import org.hibernate.testing.orm.junit.BaseUnitTest;
import org.hibernate.testing.orm.junit.FailureExpected;
import org.hibernate.testing.TestForIssue;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Specifically see if we can access a MappedSuperclass via Metamodel that is not part of a entity hierarchy
 *
 * @author Steve Ebersole
 */
@BaseUnitTest
public class MappedSuperclassType2Test {

	@Test
	@TestForIssue( jiraKey = "HHH-8534" )
	@FailureExpected( jiraKey = "HHH-8534" )
	public void testMappedSuperclassAccessNoEntity() {
		// stupid? yes.  tck does it? yes.

		final PersistenceUnitDescriptorAdapter pu = new PersistenceUnitDescriptorAdapter() {
			@Override
			public List<String> getManagedClassNames() {
				// pass in a MappedSuperclass that is not used in any entity hierarchy
				return Arrays.asList( SomeMappedSuperclass.class.getName() );
			}
		};

		final Map settings = new HashMap();
		settings.put( AvailableSettings.HBM2DDL_AUTO, "create-drop" );

		EntityManagerFactory emf = Bootstrap.getEntityManagerFactoryBuilder( pu, settings ).build();
		try {
			ManagedType<SomeMappedSuperclass> type = emf.getMetamodel().managedType( SomeMappedSuperclass.class );
			// the issue was in regards to throwing an exception, but also check for nullness
			assertNotNull( type );
		}
		finally {
			emf.close();
		}
	}
}
