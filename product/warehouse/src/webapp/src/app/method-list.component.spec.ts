import {ComponentFixture, TestBed} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {DebugElement} from '@angular/core';
import {MethodListComponent} from './method-list.component';
import {AppModule} from './app.module';
import {WarehouseService} from './warehouse.service';
import {ConfigService} from './config.service';

let component: MethodListComponent;
let fixture: ComponentFixture<MethodListComponent>;
let signatureDE: DebugElement;
let signatureElement: HTMLElement;

describe('MethodListComponent', () => {
    beforeEach(() => {

        let warehouseServiceStub = {};

        TestBed.configureTestingModule({
            imports: [AppModule],
            providers: [
                {provide: ConfigService, useValue: {}},
                {provide: WarehouseService, useValue: warehouseServiceStub}
            ] });

        fixture = TestBed.createComponent(MethodListComponent);

        component = fixture.componentInstance;

        signatureDE = fixture.debugElement.query(By.css('#signature'));
        signatureElement = signatureDE.nativeElement;

    });

    it('should display original signature', () => {
        fixture.detectChanges();
        expect(signatureElement.textContent).toBe('');
    });

    xit('should display a different signature', () => {
        component.signature = 'New Signature';
        fixture.detectChanges();
        expect(signatureElement.textContent).toBe('New Signature');
    });
});
