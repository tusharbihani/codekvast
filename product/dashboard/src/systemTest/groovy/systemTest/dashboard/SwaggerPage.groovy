package systemTest.dashboard

import geb.Page

class SwaggerPage extends Page {
    static url = '/swagger-ui.html'

    static content = {
        info_title(wait: true) { $('h2.title').text() }
    }
}