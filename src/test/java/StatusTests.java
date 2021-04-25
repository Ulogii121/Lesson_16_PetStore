import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pet.Category;
import pet.Pet;

import java.util.stream.Stream;


public class StatusTests {

    public static Stream<Arguments> param() {
        Pet myPet1 = new Pet();
        myPet1.setId(1);
        myPet1.setName("Tuzik");
        Category dogs = new Category();
        dogs.setName("Dog");
        myPet1.setCategory(dogs);
        myPet1.setStatus("available");

        Pet myPet2 = new Pet();
        myPet2.setId(2);
        myPet2.setName("Vasya");
        Category hamsters = new Category();
        hamsters.setName("Hamster");
        myPet2.setCategory(hamsters);
        myPet2.setStatus("available");

        Pet myPet3 = new Pet();
        myPet3.setId(3);
        myPet3.setName("Misha");
        Category panda = new Category();
        panda.setName("Panda");
        myPet3.setCategory(panda);
        myPet3.setStatus("available");

        return Stream.of(myPet1, myPet2, myPet3).map(Arguments::of);
    }

    @BeforeAll
    public static void baseURI() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2/pet";
    }

    //Создаём питомца
    @ParameterizedTest
    @MethodSource("param")
    public final void testPost200(final Pet myPet) {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .body(myPet)
                .post()
                .then()
                .statusCode(200);
    }

    //Создаём питомца, получаем его
    @ParameterizedTest
    @MethodSource("param")
    public final void testGet200(final Pet myPet) {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .body(myPet)
                .post()
                .then()
                .statusCode(200);

        RestAssured.get("" + myPet.getId())
                .then()
                .statusCode(200);
    }

    //Создаём питомца, удаляем его, затем пытаемся получить
    @ParameterizedTest
    @MethodSource("param")
    public final void testGet404(final Pet myPet) {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .body(myPet)
                .post()
                .then()
                .statusCode(200);

        RestAssured.delete("" + myPet.getId());

        RestAssured.get("" + myPet.getId())
                .then()
                .statusCode(404);
    }

    //Создаём питомца, присваиваем новый статус, обновляем питомца
    @ParameterizedTest
    @MethodSource("param")
    public final void testPut200(final Pet myPet) {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .body(myPet)
                .post()
                .then()
                .statusCode(200);

        myPet.setStatus("not available");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .body(myPet)
                .put()
                .then()
                .statusCode(200);
    }

    //Создаём питомца, удаляем его, присваиваем новый статус, пытаемся обновить питомца
    //Тест падает, т.к. метод PUT почему-то работает как POST
    @ParameterizedTest
    @MethodSource("param")
    public final void testPut404(final Pet myPet) {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .body(myPet)
                .post()
                .then()
                .statusCode(200);

        RestAssured.delete("" + myPet.getId());

        myPet.setStatus("not available");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .body(myPet)
                .put()
                .then()
                .statusCode(404);
    }

    //Создаём питомца, удаляем его, пытаемся получить
    @ParameterizedTest
    @MethodSource("param")
    public final void testDelete200(final Pet myPet) {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .body(myPet)
                .post()
                .then()
                .statusCode(200);

        RestAssured.delete("" + myPet.getId())
                .then()
                .statusCode(200);

        RestAssured.get("" + myPet.getId())
                .then()
                .statusCode(404);
    }

    //Создаём питомца, удаляем его, пытаемся удалить его второй раз
    @ParameterizedTest
    @MethodSource("param")
    public final void testDelete404(final Pet myPet) {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .body(myPet)
                .post()
                .then()
                .statusCode(200);

        RestAssured.delete("" + myPet.getId())
                .then()
                .statusCode(200);

        RestAssured.delete("" + myPet.getId())
                .then()
                .statusCode(404);
    }
}
