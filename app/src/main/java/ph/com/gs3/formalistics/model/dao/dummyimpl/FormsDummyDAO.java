package ph.com.gs3.formalistics.model.dao.dummyimpl;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.model.values.business.form.Form;

public class FormsDummyDAO {

	public List<Form> getUserForms(User user) {

		Form am = new Form();
		am.setId(1);
		am.setWebId(1);
		am.setName("Absence Monitoring");

		Form tam = new Form();
		tam.setId(2);
		tam.setWebId(2);
		tam.setName("Task Assignment and Monitoring");

		Form acpa = new Form();
		acpa.setId(3);
		acpa.setWebId(3);
		acpa.setName("Annual Company Policy Agreement");

		Form leave = new Form();
		leave.setId(4);
		leave.setWebId(4);
		leave.setName("Leave Request Form");

		List<Form> forms = new ArrayList<>();

		forms.add(am);
		forms.add(tam);
		forms.add(acpa);
		forms.add(leave);

		return forms;
	}

}
