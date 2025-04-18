package edu.ilkiv.lab5;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.repository.CrudRepository;
import org.springframework.beans.factory.annotation.Autowired;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaField;
import java.io.Serializable;

import static com.tngtech.archunit.lang.conditions.ArchConditions.have;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;

/*
  @author Bodya
  @project lab5
  @class Lab6ArchitectureTests
  version 1.0.0
  @since 18.04.2025 - 19:34 
*/

@SpringBootTest
public class Lab6ArchitectureTests {
    private JavaClasses applicationClasses;

    @BeforeEach
    void initialize() {
        applicationClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_ARCHIVES)
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("edu.ilkiv.lab5");
    }

    // 1. Перевірка шарової архітектури
    @Test
    void shouldFollowLayerArchitecture() {
        layeredArchitecture()
                .consideringAllDependencies()
                .layer("Controller").definedBy("..controller..")
                .layer("Service").definedBy("..service..")
                .layer("Repository").definedBy("..repository..")
                //
                .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
                .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller", "Service")
                .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
                //
                .check(applicationClasses);
    }

    // 2
    @Test
    void controllersShouldNotDependOnOtherControllers() {
        noClasses()
                .that().resideInAPackage("..controller..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..controller..")
                .because("контролери не повинні залежати від інших контролерів")
                .check(applicationClasses);
    }

    // 3
    @Test
    void repositoriesShouldNotDependOnServices() {
        noClasses()
                .that().resideInAPackage("..repository..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..service..")
                .because("репозиторії не повинні залежати від сервісів")
                .check(applicationClasses);
    }

    // 4
    @Test
    void servicesShouldNotDependOnControllers() {
        noClasses()
                .that().resideInAPackage("..service..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..controller..")
                .because("сервіси не повинні залежати від контролерів")
                .check(applicationClasses);
    }

    // 5
    @Test
    void controllersShouldBeAnnotatedWithController() {
        classes()
                .that().resideInAPackage("..controller..")
                .should().beAnnotatedWith(RestController.class)
                .orShould().beAnnotatedWith(Controller.class)
                .because("контролери повинні мати анотацію @RestController або @Controller")
                .check(applicationClasses);
    }

    // 6
    @Test
    void servicesShouldBeAnnotatedWithService() {
        classes()
                .that().resideInAPackage("..service..")
                .and().haveSimpleNameEndingWith("Service")
                .should().beAnnotatedWith(Service.class)
                .because("сервіси повинні мати анотацію @Service")
                .check(applicationClasses);
    }

    // 7
    @Test
    void repositoriesShouldBeAnnotatedWithRepository() {
        classes()
                .that().resideInAPackage("..repository..")
                .and().haveSimpleNameEndingWith("Repository")
                .should().beAnnotatedWith(Repository.class)
                .because("репозиторії повинні мати анотацію @Repository")
                .check(applicationClasses);
    }

    // 8
    @Test
    void repositoriesShouldExtendCrudRepository() {
        classes()
                .that().resideInAPackage("..repository..")
                .and().haveSimpleNameEndingWith("Repository")
                .should().beAssignableTo(CrudRepository.class)
                .because("репозиторії повинні наслідуватись від CrudRepository")
                .check(applicationClasses);
    }

    // 9
    @Test
    void controllerNamesShouldEndWithController() {
        classes()
                .that().areAnnotatedWith(RestController.class)
                .or().areAnnotatedWith(Controller.class)
                .should().haveSimpleNameEndingWith("Controller")
                .because("класи з анотацією @RestController або @Controller повинні мати назву, що закінчується на 'Controller'")
                .check(applicationClasses);
    }

    // 10
    @Test
    void serviceNamesShouldEndWithService() {
        classes()
                .that().areAnnotatedWith(Service.class)
                .should().haveSimpleNameEndingWith("Service")
                .because("класи з анотацією @Service повинні мати назву, що закінчується на 'Service'")
                .check(applicationClasses);
    }

    // 11
    @Test
    void repositoryNamesShouldEndWithRepository() {
        classes()
                .that().areAnnotatedWith(Repository.class)
                .should().haveSimpleNameEndingWith("Repository")
                .because("класи з анотацією @Repository повинні мати назву, що закінчується на 'Repository'")
                .check(applicationClasses);
    }

    // 12
    @Test
    void fieldsInServiceClassesShouldBePrivate() {
        fields()
                .that().areDeclaredInClassesThat().resideInAPackage("..service..")
                .should().bePrivate()
                .because("поля в сервісах повинні бути приватними")
                .check(applicationClasses);
    }

    // 13
    @Test
    void fieldsInControllersShouldBePrivate() {
        fields()
                .that().areDeclaredInClassesThat().resideInAPackage("..controller..")
                .should().bePrivate()
                .because("поля в контролерах повинні бути приватними")
                .check(applicationClasses);
    }

    // 14
    @Test
    void modelClassesShouldNotDependOnServices() {
        noClasses()
                .that().resideInAPackage("..model..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..service..")
                .because("моделі не повинні залежати від сервісів")
                .check(applicationClasses);
    }

    // 15
    @Test
    void modelClassesShouldNotDependOnControllers() {
        noClasses()
                .that().resideInAPackage("..model..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..controller..")
                .because("моделі не повинні залежати від контролерів")
                .check(applicationClasses);
    }

    // 16
    @Test
    void modelClassesShouldNotDependOnRepositories() {
        noClasses()
                .that().resideInAPackage("..model..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..repository..")
                .because("моделі не повинні залежати від репозиторіїв")
                .check(applicationClasses);
    }

    // 17
    @Test
    void applicationShouldNotDependOnOtherPackages() {
        noClasses()
                .that().haveSimpleNameEndingWith("Application")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("..controller..", "..service..", "..repository..")
                .because("клас Application не повинен залежати від шарів додатку")
                .check(applicationClasses);
    }

    // 18
    @Test
    void publicControllerMethodsShouldBeAnnotatedWithRequestMapping() {
        methods()
                .that().arePublic()
                .and().areDeclaredInClassesThat().resideInAPackage("..controller..")
                .should().beAnnotatedWith("org.springframework.web.bind.annotation.RequestMapping")
                .orShould().beAnnotatedWith("org.springframework.web.bind.annotation.GetMapping")
                .orShould().beAnnotatedWith("org.springframework.web.bind.annotation.PostMapping")
                .orShould().beAnnotatedWith("org.springframework.web.bind.annotation.PutMapping")
                .orShould().beAnnotatedWith("org.springframework.web.bind.annotation.DeleteMapping")
                .orShould().beAnnotatedWith("org.springframework.web.bind.annotation.PatchMapping")
                .because("публічні методи контролерів повинні мати анотації запитів")
                .check(applicationClasses);
    }

    // 19
    @Test
    void controllerMethodsShouldNotThrowGenericExceptions() {
        methods()
                .that().areDeclaredInClassesThat().resideInAPackage("..controller..")
                .should().notDeclareThrowableOfType(Exception.class)
                .because("методи контролерів не повинні кидати загальні виключення")
                .check(applicationClasses);
    }

    // 20
    /*@Test
    void entityClassesShouldBeInModelPackage() {
        classes()
                .that().areAnnotatedWith("javax.persistence.Entity")
                .or().areAnnotatedWith("jakarta.persistence.Entity")
                .should().resideInAPackage("..model..")
                .because("сутності повинні бути в пакеті моделей")
                .check(applicationClasses);
    }*/

    // 22
    @Test
    void serviceShouldNotAccessWebContext() {
        noClasses()
                .that().resideInAPackage("..service..")
                .should().accessClassesThat().resideInAPackage("org.springframework.web..")
                .because("сервіси не повинні звертатися до веб-контексту")
                .check(applicationClasses);
    }

    // 23
    @Test
    void repositoryShouldNotAccessWebContext() {
        noClasses()
                .that().resideInAPackage("..repository..")
                .should().accessClassesThat().resideInAPackage("org.springframework.web..")
                .because("репозиторії не повинні звертатися до веб-контексту")
                .check(applicationClasses);
    }

    // 24
    /*@Test
    void allPublicClassesShouldHaveTests() {
        classes()
                .that().arePublic()
                .and().resideInAnyPackage("..service..", "..controller..")
                .should(new ArchCondition<JavaClass>("мати відповідний тестовий клас") {
                    @Override
                    public void check(JavaClass javaClass, ConditionEvents events) {
                        // Тут просто заглушка
                    }
                })
                .because("всі публічні класи повинні мати тести")
                .check(applicationClasses);
    }*/

    // 25
    /*@Test
    void modelClassesShouldHaveNoArgConstructor() {
        classes()
                .that().resideInAPackage("..model..")
                .should(new ArchCondition<JavaClass>("мати конструктор без аргументів") {
                    @Override
                    public void check(JavaClass javaClass, ConditionEvents events) {
                        // Тут просто заглушка
                    }
                })
                .because("класи моделей повинні мати конструктор без аргументів для JPA")
                .check(applicationClasses);
    }*/

    // 28
    /*@Test
    void onlyServiceInterfacesShouldBeInjectedInControllers() {
        fields()
                .that().areDeclaredInClassesThat().resideInAPackage("..controller..")
                .and().areAnnotatedWith(Autowired.class)
                .and().haveRawType(resideInAPackage("..service.."))
                .should().beInterfaces()
                .because("в контролери повинні впроваджуватися лише інтерфейси сервісів")
                .check(applicationClasses);
    } */

    // 29
    @Test
    void busEntityShouldExist() {
        classes()
                .that().haveSimpleName("Bus")
                .should().resideInAPackage("..model..")
                .because("сутність Bus повинна бути в пакеті моделей")
                .check(applicationClasses);
    }

    // 30
    @Test
    void busServiceShouldDependOnBusRepository() {
        classes()
                .that().haveSimpleName("BusService")
                .or().haveSimpleName("BusServiceImpl")
                .should().dependOnClassesThat().haveSimpleName("BusRepository")
                .because("сервіс автобусів повинен залежати від репозиторію автобусів")
                .check(applicationClasses);
    }

    // 33
    /*@Test
    void controllersShouldHandleExceptions() {
        classes()
                .that().resideInAPackage("..controller..")
                .should().containAtLeastOneMethodThat().isAnnotatedWith("org.springframework.web.bind.annotation.ExceptionHandler")
                .orShould().beNestedClasses().orShould(have -> have)
                .because("контролери повинні обробляти виключення")
                .check(applicationClasses);
    } */

    // 34
    /*@Test
    void modelsShouldFollowJavaBeanConventions() {
        classes()
                .that().resideInAPackage("..model..")
                .should(new ArchCondition<JavaClass>("дотримуватись конвенцій JavaBean") {
                    @Override
                    public void check(JavaClass javaClass, ConditionEvents events) {
                        // Тут просто заглушка
                    }
                })
                .because("моделі повинні дотримуватись конвенцій JavaBean")
                .check(applicationClasses);
    }*/

    // 37
    @Test
    void noDirectAccessToDatabase() {
        noClasses()
                .that().resideInAPackage("..service..")
                .or().resideInAPackage("..controller..")
                .should().accessClassesThat().resideInAPackage("java.sql..")
                .because("прямий доступ до бази даних повинен бути тільки через репозиторії")
                .check(applicationClasses);
    }
}
