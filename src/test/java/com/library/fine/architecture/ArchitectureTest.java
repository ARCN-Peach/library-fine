package com.library.fine.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

class ArchitectureTest {

    private static JavaClasses classes;

    private static final String DOMAIN = "com.library.fine.domain..";
    private static final String APPLICATION = "com.library.fine.application..";
    private static final String INFRASTRUCTURE = "com.library.fine.infrastructure..";
    private static final String INTERFACES = "com.library.fine.interfaces..";

    @BeforeAll
    static void loadClasses() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.library.fine");
    }

    @Test
    void domain_does_not_depend_on_spring() {
        ArchRule rule = noClasses().that().resideInAPackage(DOMAIN)
                .should().dependOnClassesThat().resideInAPackage("org.springframework..");

        rule.check(classes);
    }

    @Test
    void domain_does_not_depend_on_infrastructure() {
        ArchRule rule = noClasses().that().resideInAPackage(DOMAIN)
                .should().dependOnClassesThat().resideInAPackage(INFRASTRUCTURE);

        rule.check(classes);
    }

    @Test
    void domain_does_not_depend_on_interfaces() {
        ArchRule rule = noClasses().that().resideInAPackage(DOMAIN)
                .should().dependOnClassesThat().resideInAPackage(INTERFACES);

        rule.check(classes);
    }

    @Test
    void application_does_not_depend_on_infrastructure() {
        ArchRule rule = noClasses().that().resideInAPackage(APPLICATION)
                .should().dependOnClassesThat().resideInAPackage(INFRASTRUCTURE);

        rule.check(classes);
    }

    @Test
    void application_does_not_depend_on_interfaces() {
        ArchRule rule = noClasses().that().resideInAPackage(APPLICATION)
                .should().dependOnClassesThat().resideInAPackage(INTERFACES);

        rule.check(classes);
    }

    @Test
    void interfaces_does_not_access_persistence_directly() {
        ArchRule rule = noClasses().that().resideInAPackage(INTERFACES)
                .should().dependOnClassesThat().resideInAPackage("com.library.fine.infrastructure.persistence..");

        rule.check(classes);
    }

    @Test
    void layered_architecture_is_respected() {
        ArchRule rule = layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                .layer("Domain").definedBy(DOMAIN)
                .layer("Application").definedBy(APPLICATION)
                .layer("Infrastructure").definedBy(INFRASTRUCTURE)
                .layer("Interfaces").definedBy(INTERFACES)
                .whereLayer("Domain").mayNotAccessAnyLayer()
                .whereLayer("Application").mayOnlyAccessLayers("Domain")
                .whereLayer("Interfaces").mayOnlyAccessLayers("Application", "Domain");

        rule.check(classes);
    }
}
