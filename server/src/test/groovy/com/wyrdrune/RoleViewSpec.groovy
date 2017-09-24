import com.wyrdrune.Role
import grails.plugin.json.view.test.JsonViewTest
import spock.lang.Specification

class RoleViewSpec extends Specification implements JsonViewTest {
  void "Test render of Role GSON view"() {
    when: "A gson view is rendered for a Role instance"
    def result = render(template: "/role/role", model: [role: new Role(authority: 'ROLE_USER_1')])

    then: "The json is correct"
    result.json == [authority: 'ROLE_USER_1']
  }
}