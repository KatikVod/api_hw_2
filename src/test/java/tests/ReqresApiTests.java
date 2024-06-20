package tests;

import models.GetUserResponseModel;
import models.RegistrationBodyModel;
import models.RegistrationErrorModel;
import models.RegistrationResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.ReqresApiSpec.*;

public class ReqresApiTests extends TestBase {
    @Test
    @DisplayName("Успешная регистрация пользователя")
    void successfulRegistrationTest() {
        RegistrationBodyModel authData = new RegistrationBodyModel();
        authData.setEmail("eve.holt@reqres.in");
        authData.setPassword("pistol");

        RegistrationResponseModel response = step("Отправляем запрос", () ->
                given(requestSpec)
                        .body(authData)
                        .when()
                        .post("/register")
                        .then()
                        .spec(successful200ResponseSpec)
                        .extract().as(RegistrationResponseModel.class));

        step("Проверяем, что в ответе получен токен", () ->
                assertThat(response.getToken()).isNotEmpty());
    }

    @Test
    @DisplayName("Регистрация пользователя без пароля")
    void registrationWithoutPasswordTest() {
        RegistrationBodyModel authData = new RegistrationBodyModel();
        authData.setEmail("john.holt@reqres.in");

        RegistrationErrorModel response = step("Отправляем запрос", () ->
                given(requestSpec)
                        .body(authData)
                        .when()
                        .post("/register")
                        .then()
                        .spec(error400ResponseSpec)
                        .extract().as(RegistrationErrorModel.class));

        step("Проверяем ошибку, полученную в ответе", () ->
                assertThat(response.getError()).isEqualTo("Missing password"));
    }

    @Test
    @DisplayName("Регистрация пользователя без email")
    void registrationWithoutEmailTest() {
        RegistrationBodyModel authData = new RegistrationBodyModel();
        authData.setPassword("mypassword");

        RegistrationErrorModel response = step("Отправляем запрос", () ->
                given(requestSpec)
                        .body(authData)
                        .when()
                        .post("/register")
                        .then()
                        .spec(error400ResponseSpec)
                        .extract().as(RegistrationErrorModel.class));

        step("Проверяем ошибку, полученную в ответе", () ->
                assertThat(response.getError()).isEqualTo("Missing email or username"));
    }

    @Test
    @DisplayName("Регистрация пользователя с некорректным email")
    void registrationWithWrongEmailTest() {
        RegistrationBodyModel authData = new RegistrationBodyModel();
        authData.setEmail("1");
        authData.setPassword("pass");

        RegistrationErrorModel response = step("Отправляем запрос", () ->
                given(requestSpec)
                        .body(authData)
                        .when()
                        .post("/register")
                        .then()
                        .spec(error400ResponseSpec)
                        .extract().as(RegistrationErrorModel.class));
        step("Проверяем ошибку, полученную в ответе", () ->
                assertThat(response.getError()).isEqualTo("Note: Only defined users succeed registration"));
    }

    @Test
    @DisplayName("Успешное получение пользователя по id")
    void getSingleUserTest() {

        GetUserResponseModel response = step("Отправляем запрос", () ->
                given(requestSpec)
                        .when()
                        .get("/users/2")
                        .then()
                        .spec(successful200ResponseSpec)
                        .extract().as(GetUserResponseModel.class));

        step("Проверяем id пользователя в ответе", () ->
                assertThat(response.getData().getId()).isEqualTo("2"));
    }

    @Test
    @DisplayName("Неуспешное получение пользователя по id")
    void getNonexistentUserTest() {

        GetUserResponseModel response = step("Отправляем запрос", () ->
                given(requestSpec)
                        .when()
                        .get("/users/30")
                        .then()
                        .spec(error404ResponseSpec)
                        .extract().as(GetUserResponseModel.class));
    }

    @Test
    @DisplayName("Успешное удаление пользователя")
    public void deleteUserTest() {
        step("Отправляем запрос", () ->
                given(requestSpec)
                        .when()
                        .delete("api/users/2")
                        .then()
                        .spec(successful204ResponseSpec));
    }
}
