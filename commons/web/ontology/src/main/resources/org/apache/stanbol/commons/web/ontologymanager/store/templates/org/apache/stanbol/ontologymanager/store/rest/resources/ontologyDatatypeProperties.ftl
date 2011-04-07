<#import "/imports/common.ftl" as common>
<#import "/imports/prevNextButtons.ftl" as buttons>
<@common.page title="Datatype Properties Of ${it.metadata.ontologyMetaInformation.URI}" hasrestapi=true>
<div class="panel" id="webview">
		<h4 class="addHeader">Create a new Datatype Property</h4>
		<form method="POST" accept-charset="utf-8" accept="text/html" enctype="application/x-www-form-urlencoded">
		  <fieldset>
			  <legend>Enter Datatype Property URI to be created</legend>
			  <p>Datatype Property URI: <textarea rows="1" name="datatypePropertyURI"></textarea></p>
			  <p><input type="submit" value="Create Datatype Property"></p>
		  </fieldset>
		</form>
	<#if it.metadata.propertyMetaInformation?size == 0>
	<br>
	<p><em>Currently there is no classes in this ontology.</em></p>
		
	<#else>
	<fieldset>
		<legend><b>Datatype Properties</b></legend>
		<#list it.metadata.propertyMetaInformation?sort_by("URI") as prop>
			<div class="ontology ontDPropList ${prop_index}"> 
			<div class="collapsed">
					<a class="imgOntDataProp" href="${prop.href}">${prop.URI}</a>
					<button class="delete" title="Delete ${prop.URI}" onClick="javascript: deleteClass('${prop.href}')"></button>
					<div class="ontologyHeader"></div>
				<ul class= "ontologyCollapsable">
					<li><b>Description:</b> ${prop.description}</li>
					<li><b>Namespace:</b> ${prop.namespace}</li>
					<li><b>Local Name:</b> ${prop.localName}</li>
				</ul>
			</div>	
		</div>
		</#list>
			<@buttons.prevNextButtons className="ontDPropList"/>
	</fieldset>
	</#if>
<script>
	PAGING.adjustVisibility("ontDPropList");

	function deleteClass(uri)
	{
		xmlhttp=new XMLHttpRequest();
		xmlhttp.open('DELETE',uri,false);
		xmlhttp.send();
		location.reload('true');
	}

	$(".ontology .ontologyHeader").click(function () {
	  $(this).parent().toggleClass("collapsed");
	}); 
</script>
</div>

<div class="panel" id="restapi" style="display: none;">
<h3>Getting datatype properties of ontology</h3>
<pre>
curl -i -X GET -H "Accept:application/xml" http://localhost:8080/persistencestore/${it.metadata.ontologyMetaInformation.href}/datatypeProperties
</pre>
<p>Response :</p>

<pre>
HTTP/1.1 200 OK
Content-Type: application/xml
Transfer-Encoding: chunked
Server: Jetty(6.1.x)

&lt;?xml version="1.0" encoding="UTF-8" standalone="yes"?&gt;
&lt;ns1:DatatypePropertiesForOntology xmlns:ns1="model.rest.persistence.iks.srdc.com.tr" xmlns:ns2="http://www.w3.org/1999/xlink"&gt;
    &lt;ns1:OntologyMetaInformation ns2:href="ontologies/http://dbpedia.org/ontology/"&gt;
        &lt;ns1:URI&gt;http://dbpedia.org/ontology/&lt;/ns1:URI&gt;
        &lt;ns1:Description&gt;&lt;/ns1:Description&gt;
    &lt;/ns1:OntologyMetaInformation&gt;
    &lt;ns1:PropertyMetaInformation ns2:href="ontologies/http://dbpedia.org/ontology//datatypeProperties/http://dbpedia.org/ontology/academyaward"&gt;
        &lt;ns1:URI&gt;http://dbpedia.org/ontology/academyaward&lt;/ns1:URI&gt;
        &lt;ns1:Description&gt;&lt;/ns1:Description&gt;
        &lt;ns1:Namespace&gt;http://dbpedia.org/ontology/&lt;/ns1:Namespace&gt;
        &lt;ns1:LocalName&gt;academyaward&lt;/ns1:LocalName&gt;
    &lt;/ns1:PropertyMetaInformation&gt;
&lt;/ns1:DatatypePropertiesForOntology&gt;
</pre>
<h3>Creating a new datatypeProperty</h3>
<pre>
curl -i -X POST -H "Accept:application/xml" --data-urlencode datatypePropertyURI=http://iks-project.eu/datatypeProps#SampleDatatypeProperty http://localhost:8080/persistencestore/${it.metadata.ontologyMetaInformation.href}/datatypeProperties
</pre>
<p>Response</p>
<pre>
HTTP/1.1 200 OK
Content-Type: application/xml
Transfer-Encoding: chunked
Server: Jetty(6.1.x)

&lt;?xml version="1.0" encoding="UTF-8" standalone="yes"?&gt;
&lt;ns1:PropertyMetaInformation xmlns:ns1="model.rest.persistence.iks.srdc.com.tr" xmlns:ns2="http://www.w3.org/1999/xlink" ns2:href="ontologies/http://dbpedia.org/ontology//datatypeP
roperties/http://iks-project.eu/datatypeProps/SampleDatatypeProperty"&gt;
    &lt;ns1:URI&gt;http://iks-project.eu/datatypeProps#SampleDatatypeProperty&lt;/ns1:URI&gt;
    &lt;ns1:Description&gt;&lt;/ns1:Description&gt;
    &lt;ns1:Namespace&gt;http://iks-project.eu/datatypeProps#&lt;/ns1:Namespace&gt;
    &lt;ns1:LocalName&gt;SampleDatatypeProperty&lt;/ns1:LocalName&gt;
&lt;/ns1:PropertyMetaInformation&gt;
</pre>
</div>
</@common.page>