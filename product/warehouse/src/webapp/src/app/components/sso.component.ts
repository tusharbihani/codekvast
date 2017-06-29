import {ActivatedRoute, Router} from '@angular/router';
import {Component, OnInit} from '@angular/core';
import {StateService} from '../services/state.service';

@Component({
    selector: 'ck-sso',
    template: '',
})

export class SsoComponent implements OnInit {

    constructor(private route: ActivatedRoute, private router: Router, private stateService: StateService) {
    }

    ngOnInit(): void {
        let token = this.route.snapshot.params['token'];
        let parts = token.split('\.');
        if (parts.length === 3) {
            // header = parts[0]
            let payload = JSON.parse(atob(parts[1]));
            console.log('payload=%o', payload);
            // signature = parts[2]

            let Boomerang = window['Boomerang'];
            let sourceApp = 'unknown';
            if (payload.source === 'heroku') {
                let navData = this.route.snapshot.params['navData'];
                let args = JSON.parse(atob(navData));
                console.log('navData=%o', args);
                sourceApp = args.app || args.appname;
                Boomerang.init({
                    app: sourceApp,
                    addon: 'codekvast'
                });
            } else {
                Boomerang.reset();
            }

            this.stateService.setLoggedInAs(token, payload.sub, payload.customerName, payload.email, payload.source, sourceApp);
        }
        // noinspection JSIgnoredPromiseFromCall
        this.router.navigate(['']);
    }
}
