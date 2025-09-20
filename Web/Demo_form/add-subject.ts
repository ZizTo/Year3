import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject } from '../subject';
import { SubjectService } from '../subject.service';
import { Observable } from "rxjs";
import { RouterOutlet, ActivatedRoute, RouterLink, Router } from '@angular/router';
@Component({
  selector: 'app-add-subject',
  imports: [RouterOutlet, FormsModule, RouterLink, CommonModule],
  templateUrl: './add-subject.html',
  styleUrl: './add-subject.css'
})
export class AddSubject {
	subject : Subject;
	subjects$: Observable<Subject[]>;
	@Input() subject_id: number;
	@Input() subject_name: string;
	@Input() subject_lector: string;
	@Input() isEditing: boolean = false;

	constructor(private service: SubjectService,
	          private route: ActivatedRoute,
	          private router: Router,
	          private service1: FirestoreService,
	          ) {
	 this.subject = {id:0, name:'', teacher:''};
	}
	ngOnInit() {
		  this.subjects$ =  this.service.getSubjects();
	
		}
	
	onSubmit() {
		this.subject.id=this.subject_id;
		this.subject.name=this.subject_name;
		this.subject.teacher=this.subject_lector;
		console.log("on submit");
		console.log(this.subject.name);
		console.log(this.isEditing);
		console.log(this.subject);
		    this.service.create(this.subject)
		    .then (() => this.gotoSubjectList());
		  
		  }
	onDelete(){
		this.service.delete(this.subject)
	    .then (() => this.gotoSubjectList());
	}
	
	gotoSubjectList() {

	    this.router.navigate(['/subject-center']);

	  }
	
}
