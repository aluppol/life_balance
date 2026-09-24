package com.luppol.lifebalance;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "com.luppol.lifebalance", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {
    @ArchTest
    static final ArchRule dependenciesPointInward = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Domain").definedBy("com.luppol.lifebalance.domain..")
            .layer("Application").definedBy("com.luppol.lifebalance.application..")
            .layer("Persistence").definedBy("com.luppol.lifebalance.adapter.persistence..")
            .layer("Web").definedBy("com.luppol.lifebalance.adapter.web..")
            .layer("Composition").definedBy("com.luppol.lifebalance")
            .whereLayer("Composition").mayNotBeAccessedByAnyLayer()
            .whereLayer("Web").mayNotBeAccessedByAnyLayer()
            .whereLayer("Persistence").mayNotBeAccessedByAnyLayer()
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Web", "Composition")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Persistence", "Web", "Composition");

    @ArchTest
    static final ArchRule domainIsPlainJava = noClasses()
            .that().resideInAPackage("com.luppol.lifebalance.domain..")
            .should().dependOnClassesThat().resideOutsideOfPackages("java..", "com.luppol.lifebalance.domain..");

    @ArchTest
    static final ArchRule applicationIsPlainJava = noClasses()
            .that().resideInAPackage("com.luppol.lifebalance.application..")
            .should().dependOnClassesThat().resideOutsideOfPackages("java..", "com.luppol.lifebalance.domain..",
                    "com.luppol.lifebalance.application..");

    @ArchTest
    static final ArchRule domainFeaturesHaveNoCycles = slices()
            .matching("com.luppol.lifebalance.domain.(*)..")
            .should().beFreeOfCycles();

    @ArchTest
    static final ArchRule applicationFeaturesHaveNoCycles = slices()
            .matching("com.luppol.lifebalance.application.(*)..")
            .should().beFreeOfCycles();

    @ArchTest
    static final ArchRule controllersLiveInTheWebAdapter = classes()
            .that().areAnnotatedWith(RestController.class)
            .should().resideInAPackage("com.luppol.lifebalance.adapter.web.api..");

    @ArchTest
    static final ArchRule entitiesLiveInThePersistenceAdapter = classes()
            .that().areAnnotatedWith(Entity.class)
            .should().resideInAPackage("com.luppol.lifebalance.adapter.persistence..");

    @ArchTest
    static final ArchRule useCasesAreSplitIntoCommandsAndQueries = classes()
            .that().resideInAPackage("com.luppol.lifebalance.application..")
            .and().haveSimpleNameEndingWith("Service")
            .should().haveSimpleNameEndingWith("CommandService").orShould().haveSimpleNameEndingWith("QueryService");

    @ArchTest
    static final ArchRule noFieldInjection = noFields().should().beAnnotatedWith(Autowired.class);
}
