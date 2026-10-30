package com.example.seatinventory;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.example.seatinventory", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule domainDependsOnlyOnTheJdk = classes()
        .that().resideInAPackage("..domain..")
        .should().onlyDependOnClassesThat().resideInAnyPackage("..domain..", "java..")
        .allowEmptyShould(true);

    @ArchTest
    static final ArchRule applicationDoesNotDependOnOuterLayers = noClasses()
        .that().resideInAPackage("..application..")
        .should().dependOnClassesThat().resideInAnyPackage("..adapter..", "..infrastructure..", "..config..")
        .allowEmptyShould(true);

    @ArchTest
    static final ArchRule adapterGoesThroughInboundPorts = noClasses()
        .that().resideInAPackage("..adapter..")
        .should().dependOnClassesThat().resideInAnyPackage("..infrastructure..", "..domain..")
        .allowEmptyShould(true);

    @ArchTest
    static final ArchRule persistenceEntitiesStayInInfrastructure = noClasses()
        .that().resideOutsideOfPackage("..infrastructure..")
        .should().dependOnClassesThat().haveSimpleNameStartingWith("Jpa")
        .allowEmptyShould(true);
}
