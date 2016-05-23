import {Component} from 'angular2/core';
import {HTTP_PROVIDERS}    from 'angular2/http';
import {ConfigService} from './config.service';
import {MethodListComponent} from './method-list.component';

@Component({
    selector: 'ck-app', templateUrl: 'app/app.component.html',
    providers: [HTTP_PROVIDERS, ConfigService],
    directives: [MethodListComponent]
})
export class AppComponent {

    now: Date = new Date();
    apiPrefix;
    version;

    constructor(_config: ConfigService) {
        this.apiPrefix = _config.getApiPrefix();
        this.version = _config.getVersion();
    }
}
